package com.yihsiang.video_list_animation

import android.content.res.Resources
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.view.doOnLayout
import androidx.core.view.doOnNextLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import com.yihsiang.video_list_animation.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.show.setOnClickListener {
            supportFragmentManager.commit {
                fragment = VideoListFragment()
                fragment?.let { f -> replace(R.id.container, f) }

            }
        }

        supportFragmentManager.setFragmentResultListener("close", this) { _, _ ->
            supportFragmentManager.commit {
                fragment?.let { f -> remove(f) }
            }
        }
    }
}