package com.yihsiang.video_list_animation

import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.core.view.doOnNextLayout
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.RecyclerView
import com.yihsiang.video_list_animation.databinding.FragmentVideoListBinding
import kotlin.math.roundToInt

class VideoListFragment : Fragment() {

    private lateinit var binding: FragmentVideoListBinding

    private val flyAnimation by lazy {
        val spacing = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f,
            Resources.getSystem().displayMetrics).roundToInt()
        VideoListFlyAnimation(binding.videoList, spacing)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = VideoAdapter()
        binding.videoList.adapter = adapter
        binding.videoList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val margin = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    5f,
                    Resources.getSystem().displayMetrics
                ).roundToInt()
                when (parent.getChildAdapterPosition(view)) {
                    0 -> {
                        outRect.right = margin
                    }
                    else -> {
                        outRect.left = margin
                        outRect.right = margin
                    }
                }
            }
        })

        binding.rightArrow.setOnClickListener {
            flyAnimation.flyOut()
            flyAnimation.onFlyOutEnd = {
                setFragmentResult("close", bundleOf())
            }
        }

        val data = (0..100).map { it.toString() }
        adapter.submitList(data) {
            binding.videoList.doOnPreDraw {
                flyAnimation.targetCenterPosition = 10
                flyAnimation.flyIn()
            }
        }
    }
}