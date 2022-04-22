package com.yihsiang.video_list_animation

import android.animation.Animator
import android.view.ViewPropertyAnimator

inline fun ViewPropertyAnimator.addListener(
    crossinline onEnd: (animator: Animator) -> Unit = {},
    crossinline onStart: (animator: Animator) -> Unit = {},
    crossinline onCancel: (animator: Animator) -> Unit = {},
    crossinline onRepeat: (animator: Animator) -> Unit = {}
): ViewPropertyAnimator {
    val listener = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animator: Animator) = onRepeat(animator)

        override fun onAnimationEnd(animator: Animator) {
            onEnd(animator)
            // 取消或結束需要移除監聽器，否則同一個View在做動畫會重複被呼叫
            setListener(null)
        }

        override fun onAnimationCancel(animator: Animator) {
            onCancel(animator)
            setListener(null)
        }

        override fun onAnimationStart(animator: Animator) = onStart(animator)
    }
    setListener(listener)
    return this
}