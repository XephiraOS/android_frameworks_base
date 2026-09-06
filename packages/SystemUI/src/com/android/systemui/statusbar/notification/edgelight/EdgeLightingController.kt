/*
 * Copyright (C) 2026 XephiraOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.systemui.statusbar.notification.edgelight

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager

class EdgeLightingController(private val context: Context) {

    companion object {
        const val SETTING_ENABLED = "edge_lighting_enabled"
        const val SETTING_COLOR = "edge_lighting_color"
    }

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var edgeView: EdgeLightingView? = null
    private var isViewAttached = false

    fun isEnabled(): Boolean {
        return Settings.System.getInt(context.contentResolver, SETTING_ENABLED, 1) == 1
    }

    fun onNotificationPosted(appColor: Int? = null) {
        if (!isEnabled()) return

        val colorStr = Settings.System.getString(context.contentResolver, SETTING_COLOR)
        val targetColor = when (colorStr) {
            "crimson" -> Color.parseColor("#E60026")
            "cyan" -> Color.parseColor("#00F2FE")
            "violet" -> Color.parseColor("#8B5CF6")
            "amber" -> Color.parseColor("#F59E0B")
            else -> appColor ?: Color.parseColor("#00F2FE")
        }

        ensureAttached()
        edgeView?.setGlowColor(targetColor)
        edgeView?.startPulseAnimation(repeatCount = 1) {
            detachView()
        }
    }

    private fun ensureAttached() {
        if (isViewAttached) return

        edgeView = EdgeLightingView(context)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_STATUS_BAR_ADDITIONAL,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            title = "XephiraEdgeLighting"
        }

        try {
            windowManager.addView(edgeView, params)
            isViewAttached = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun detachView() {
        if (!isViewAttached || edgeView == null) return
        try {
            windowManager.removeViewImmediate(edgeView)
            isViewAttached = false
            edgeView = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
