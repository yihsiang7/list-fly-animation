package com.yihsiang.video_list_animation

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewDisabler : RecyclerView.OnItemTouchListener {

    // 禁止 RecyclerView 滑動
    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean = true

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }
}