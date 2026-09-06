/*
 * Copyright (C) 2026 XephiraOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.systemui.statusbar.capsule

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.PathInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.android.systemui.res.R

class DynamicCapsuleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    enum class State {
        IDLE,
        CHARGING,
        MEDIA,
        CALL
    }

    private val contentLayout: LinearLayout
    private val iconView: ImageView
    private val titleView: TextView
    private val subtitleView: TextView

    private var currentState = State.IDLE
    private var widthAnimator: ValueAnimator? = null

    // iOS 18 fluid spring easing: (0.175, 0.885, 0.32, 1.275)
    private val springInterpolator = PathInterpolator(0.175f, 0.885f, 0.32f, 1.275f)

    init {
        setBackgroundResource(R.drawable.bg_dynamic_capsule)
        clipToOutline = true

        contentLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            setPadding(dpToPx(12), 0, dpToPx(12), 0)
        }

        iconView = ImageView(context).apply {
            val lp = LinearLayout.LayoutParams(dpToPx(16), dpToPx(16)).apply {
                marginEnd = dpToPx(8)
            }
            layoutParams = lp
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        contentLayout.addView(iconView)

        titleView = TextView(context).apply {
            val lp = LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
            layoutParams = lp
            setTextColor(Color.WHITE)
            textSize = 12f
            setSingleLine(true)
        }
        contentLayout.addView(titleView)

        subtitleView = TextView(context).apply {
            val lp = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            layoutParams = lp
            setTextColor(Color.parseColor("#00F2FE"))
            textSize = 11f
            setSingleLine(true)
        }
        contentLayout.addView(subtitleView)

        addView(contentLayout)
        visibility = View.GONE
    }

    fun showCharging(wattage: Int, batteryPct: Int) {
        currentState = State.CHARGING
        iconView.setImageResource(android.R.drawable.ic_lock_idle_charging)
        iconView.setColorFilter(Color.parseColor("#00F2FE"))
        titleView.text = if (wattage > 65) "SUPERVOOC ${wattage}W" else "Fast Charging ${wattage}W"
        subtitleView.text = "$batteryPct%"
        subtitleView.setTextColor(Color.parseColor("#10B981"))
        animateTo(targetWidth = dpToPx(175), targetHeight = dpToPx(34))
    }

    fun showMedia(track: String, artist: String) {
        currentState = State.MEDIA
        iconView.setImageResource(android.R.drawable.ic_media_play)
        iconView.setColorFilter(Color.parseColor("#E60026"))
        titleView.text = track
        subtitleView.text = artist
        subtitleView.setTextColor(Color.parseColor("#B3FFFFFF"))
        animateTo(targetWidth = dpToPx(210), targetHeight = dpToPx(34))
    }

    fun showCall(duration: String) {
        currentState = State.CALL
        iconView.setImageResource(android.R.drawable.stat_sys_phone_call)
        iconView.setColorFilter(Color.parseColor("#10B981"))
        titleView.text = "Active Call"
        subtitleView.text = duration
        subtitleView.setTextColor(Color.WHITE)
        animateTo(targetWidth = dpToPx(160), targetHeight = dpToPx(34))
    }

    fun collapse() {
        if (visibility == View.GONE) return
        currentState = State.IDLE
        animateTo(targetWidth = dpToPx(32), targetHeight = dpToPx(32), onEnd = {
            visibility = View.GONE
        })
    }

    private fun animateTo(targetWidth: Int, targetHeight: Int, onEnd: (() -> Unit)? = null) {
        visibility = View.VISIBLE
        widthAnimator?.cancel()

        val startW = if (width > 0) width else dpToPx(32)
        val startH = if (height > 0) height else dpToPx(32)

        widthAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 320
            interpolator = springInterpolator
            addUpdateListener { anim ->
                val fraction = anim.animatedValue as Float
                layoutParams = layoutParams.apply {
                    width = (startW + (targetWidth - startW) * fraction).toInt()
                    height = (startH + (targetHeight - startH) * fraction).toInt()
                }
            }
            onEnd?.let { endAction ->
                addListener(object : android.animation.AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: android.animation.Animator) {
                        endAction()
                    }
                })
            }
            start()
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}
