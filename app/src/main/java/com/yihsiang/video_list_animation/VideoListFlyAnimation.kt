package com.yihsiang.video_list_animation

import android.util.Log
import android.view.*
import androidx.recyclerview.widget.*
import kotlin.math.*

class VideoListFlyAnimation(
    private val recyclerView: RecyclerView,
    private val itemSpacing: Int // px
) {

    var onFlyOutEnd: (() -> Unit)? = null

    var targetCenterPosition = 0

    private val disabler = RecyclerViewDisabler()

    private var state = RecyclerView.SCROLL_STATE_IDLE

    val canExecuteAnimation: Boolean
        get() = state == RecyclerView.SCROLL_STATE_IDLE

    init {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                state = newState
            }
        })
    }

    fun flyIn() {
        executeAnimation(true)
    }

    fun flyOut() {
        executeAnimation(false)
    }

    private fun executeAnimation(flyIn: Boolean) {
        if (!canExecuteAnimation) return

        // 先將列表顯示出來才能算出可視範圍的數量
        if (flyIn) recyclerView.visibility = View.VISIBLE

        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return

        if (flyIn) {
            // 如果使用 OnScrollListener 在滑動過小的距離或滑動到一樣的位置會無法觸發
            recyclerView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(v: View?, left: Int, top: Int,
                    right: Int, bottom: Int, oldLeft: Int, oldTop: Int,
                    oldRight: Int, oldBottom: Int
                ) {
                    recyclerView.removeOnLayoutChangeListener(this)
                    executeItemViewsAnimation(true)
                }
            })

            val totalListSize = recyclerView.width
            val halfListSize = totalListSize / 2f
            // 因為每個 item 大小都一樣，直接取第一個寬度
            val itemViewSize = recyclerView.findViewHolderForAdapterPosition(0)
                ?.itemView?.width ?: 0
            val halfItemSize = itemViewSize / 2f
            val beforeTargetCenterListItemCount = targetCenterPosition
            val distanceBetweenStartEdge = beforeTargetCenterListItemCount * itemViewSize +
                beforeTargetCenterListItemCount * itemSpacing
            val totalListCount = recyclerView.adapter?.itemCount ?: 0
            val afterTargetCenterListItemCount = totalListCount - (targetCenterPosition + 1)
            val distanceBetweenEndEdge = if (afterTargetCenterListItemCount <= 0) {
                0
            } else {
                afterTargetCenterListItemCount * itemViewSize +
                    (afterTargetCenterListItemCount + 1/*最右邊也有間距*/) * itemSpacing
            }
            val distanceBetweenEdgeOffset = (halfListSize - halfItemSize).roundToInt()

            // 需要計算出靠右或靠左無法滑動到置中的 item
            if (distanceBetweenStartEdge > distanceBetweenEdgeOffset &&
                distanceBetweenEndEdge > distanceBetweenEdgeOffset) {
                layoutManager.scrollToPositionWithOffset(targetCenterPosition, distanceBetweenEdgeOffset)
            } else {
                layoutManager.scrollToPosition(targetCenterPosition)
            }
        } else {
            executeItemViewsAnimation(false)
        }
    }

    private fun executeItemViewsAnimation(flyIn: Boolean) {
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return

        recyclerView.alpha = 1f

        val firstPosition = layoutManager.findFirstVisibleItemPosition()
        val lastPosition = layoutManager.findLastVisibleItemPosition()
        if (firstPosition == RecyclerView.NO_POSITION ||
            lastPosition == RecyclerView.NO_POSITION) return

        (firstPosition..lastPosition).forEach { index ->
            val itemView = layoutManager.findViewByPosition(index) ?: return@forEach

            // 所有可視範圍item的寬度 + 每個item間的間距
            val offset = ((lastPosition - firstPosition + 1) * itemView.width) +
                ((lastPosition - firstPosition) * itemSpacing)

            // 在開啟列表時，item 會在原本的可視範圍位置，所以需要位移到左邊在執行動畫
            if (flyIn) {
                itemView.x = -offset.toFloat()
                itemView.alpha = 0f
            }

            val translationX = if (flyIn) 0 else -offset
            val alpha = if (flyIn) 1f else 0f

            itemView.animate()
                .translationX(translationX.toFloat())
                .alpha(alpha)
                .setDuration(600L)
                .apply {
                    if (firstPosition == index) {
                        addListener(onStart = {
                            recyclerView.addOnItemTouchListener(disabler)
                        })
                    }
                    if (lastPosition == index) {
                        addListener(onEnd = {
                            if (!flyIn) {
                                recyclerView.visibility = View.INVISIBLE
                                recyclerView.alpha = 0f
                                itemView.alpha = 1f
                                onFlyOutEnd?.invoke()
                            }
                            recyclerView.removeOnItemTouchListener(disabler)
                        })
                    }
                }
                .start()
        }
    }
}