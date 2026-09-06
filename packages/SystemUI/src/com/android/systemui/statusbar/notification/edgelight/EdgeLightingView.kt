/*
 * Copyright (C) 2026 XephiraOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.systemui.statusbar.notification.edgelight

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

class EdgeLightingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dpToPx(4.5f)
        strokeCap = Paint.Cap.ROUND
    }

    private var glowColor: Int = Color.parseColor("#00F2FE")
    private var pulseAnimator: ValueAnimator? = null
    private var currentAlpha = 0f
    private val cornerRadius = dpToPx(32f)

    fun setGlowColor(color: Int) {
        glowColor = color
        strokePaint.color = color
        invalidate()
    }

    fun startPulseAnimation(repeatCount: Int = 2, onFinished: (() -> Unit)? = null) {
        pulseAnimator?.cancel()
        visibility = VISIBLE

        pulseAnimator = ValueAnimator.ofFloat(0f, 1f, 0f).apply {
            duration = 1400
            this.repeatCount = repeatCount
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { anim ->
                currentAlpha = anim.animatedValue as Float
                strokePaint.alpha = (currentAlpha * 255).toInt()
                invalidate()
            }
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    visibility = GONE
                    onFinished?.invoke()
                }
            })
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (currentAlpha <= 0f) return

        val inset = strokePaint.strokeWidth / 2f
        val rect = RectF(inset, inset, width - inset, height - inset)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, strokePaint)
    }

    private fun dpToPx(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }
}
