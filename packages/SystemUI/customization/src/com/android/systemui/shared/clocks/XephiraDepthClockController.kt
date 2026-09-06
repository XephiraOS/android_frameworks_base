/*
 * Copyright (C) 2026 XephiraOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.systemui.shared.clocks

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.android.systemui.plugins.keyguard.data.model.AlarmData
import com.android.systemui.plugins.keyguard.data.model.WeatherData
import com.android.systemui.plugins.keyguard.data.model.ZenData
import com.android.systemui.plugins.keyguard.ui.clocks.ClockAnimations
import com.android.systemui.plugins.keyguard.ui.clocks.ClockAxisStyle
import com.android.systemui.plugins.keyguard.ui.clocks.ClockConfig
import com.android.systemui.plugins.keyguard.ui.clocks.ClockController
import com.android.systemui.plugins.keyguard.ui.clocks.ClockEventListeners
import com.android.systemui.plugins.keyguard.ui.clocks.ClockEvents
import com.android.systemui.plugins.keyguard.ui.clocks.ClockFaceConfig
import com.android.systemui.plugins.keyguard.ui.clocks.ClockFaceController
import com.android.systemui.plugins.keyguard.ui.clocks.ClockFaceEvents
import com.android.systemui.plugins.keyguard.ui.clocks.ClockMessageBuffers
import com.android.systemui.plugins.keyguard.ui.clocks.ClockPositionAnimationArgs
import com.android.systemui.plugins.keyguard.ui.clocks.ClockSettings
import com.android.systemui.plugins.keyguard.ui.clocks.ThemeConfig
import com.android.systemui.plugins.keyguard.ui.clocks.TimeFormatKind
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * ClockController powering the iOS 18 Spatial Depth and OnePlus Crimson Red-1 Lockscreen Clocks.
 */
class XephiraDepthClockController(
    private val ctx: Context,
    private val layoutInflater: LayoutInflater,
    private val resources: Resources,
    val settings: ClockSettings,
    private val messageBuffers: ClockMessageBuffers?,
    val clockVariant: String // "XEPHIRA_IOS_DEPTH" or "XEPHIRA_ONEPLUS_RED1" or "XEPHIRA_AETHER_SPATIAL"
) : ClockController {

    private var targetColor = settings.seedColor ?: Color.WHITE
    private val timeFormat = SimpleDateFormat("hh:mm", Locale.getDefault())

    override val config = ClockConfig(
        id = settings.clockId ?: clockVariant,
        name = when (clockVariant) {
            "XEPHIRA_ONEPLUS_RED1" -> "OnePlus Crimson 1"
            "XEPHIRA_AETHER_SPATIAL" -> "Aether Spatial Glass"
            else -> "iOS 18 Depth Clock"
        },
        description = "Xephira flagship spatial depth typography",
        isInteractive = true
    )

    private val smallClockView = createClockTextView(isLarge = false)
    private val largeClockView = createClockTextView(isLarge = true)

    override val smallClock = XephiraClockFaceController(smallClockView, isLarge = false)
    override val largeClock = XephiraClockFaceController(largeClockView, isLarge = true)

    override val events = object : ClockEvents {
        override fun onTimeTick() {
            refreshTime()
        }

        override fun onTimeZoneChanged(timeZone: TimeZone) {
            timeFormat.timeZone = timeZone
            refreshTime()
        }

        override fun onColorPaletteChanged(resources: Resources) {
            refreshTime()
        }

        override fun onSeedColorChanged(seedColor: Int?) {
            targetColor = seedColor ?: Color.WHITE
            refreshTime()
        }

        override fun onLocaleChanged(locale: Locale) {
            refreshTime()
        }

        override fun onAlarmDataChanged(alarmData: AlarmData) {}
        override fun onWeatherDataChanged(weatherData: WeatherData) {}
        override fun onZenDataChanged(zenData: ZenData) {}
    }

    override val animations = object : ClockAnimations {
        override fun enter() {}
        override fun doze(fraction: Float) {
            largeClockView.alpha = 1.0f - (fraction * 0.4f)
            smallClockView.alpha = 1.0f - (fraction * 0.4f)
        }
        override fun fold(fraction: Float) {}
        override fun charge() {}
        override fun onPositionUpdated(fromLeft: Int, direction: Int, fraction: Float) {}
        override fun onPositionAnimationArgsChanged(args: ClockPositionAnimationArgs) {}
    }

    init {
        refreshTime()
    }

    override fun initialize(buffers: ClockMessageBuffers?, initialTheme: ThemeConfig?) {
        refreshTime()
    }

    override fun dump(pw: PrintWriter) {
        pw.println("XephiraDepthClockController: variant=$clockVariant, color=$targetColor")
    }

    private fun createClockTextView(isLarge: Boolean): TextView {
        val tv = TextView(ctx)
        tv.textSize = if (isLarge) 82f else 32f
        tv.typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)
        tv.letterSpacing = -0.03f
        tv.setTextColor(targetColor)
        return tv
    }

    private fun refreshTime() {
        val now = Date()
        val formatted = timeFormat.format(now)

        if (clockVariant == "XEPHIRA_ONEPLUS_RED1") {
            // Style every occurrence of '1' in OnePlus Crimson Red (#E60026)
            val span = SpannableString(formatted)
            val crimsonColor = Color.parseColor("#E60026")
            for (i in formatted.indices) {
                if (formatted[i] == '1') {
                    span.setSpan(
                        ForegroundColorSpan(crimsonColor),
                        i,
                        i + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            largeClockView.text = span
            smallClockView.text = span
        } else if (clockVariant == "XEPHIRA_AETHER_SPATIAL") {
            // Cyan tinted numerals with soft shadow
            largeClockView.setTextColor(Color.parseColor("#00F2FE"))
            largeClockView.setShadowLayer(16f, 0f, 4f, Color.parseColor("#6600F2FE"))
            largeClockView.text = formatted
            smallClockView.text = formatted
        } else {
            // iOS 18 Bold Depth Clock
            largeClockView.setTextColor(targetColor)
            largeClockView.typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
            largeClockView.text = formatted
            smallClockView.text = formatted
        }
    }

    inner class XephiraClockFaceController(
        private val clockView: TextView,
        private val isLarge: Boolean
    ) : ClockFaceController {
        override val view: View = clockView
        override val config = ClockFaceConfig()
        override val events = object : ClockFaceEvents {
            override fun onTimeTick() {
                refreshTime()
            }
            override fun onRegionDarknessChanged(isDark: Boolean) {}
            override fun onFontSettingChanged(fontSizePx: Float) {
                clockView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSizePx)
            }
            override fun onTargetRegionChanged(targetRegion: Rect?) {}
            override fun onFontAxesChanged(fontAxes: ClockAxisStyle) {}
        }
        override val animations = object : ClockAnimations {
            override fun enter() {}
            override fun doze(fraction: Float) {}
            override fun fold(fraction: Float) {}
            override fun charge() {}
            override fun onPositionUpdated(fromLeft: Int, direction: Int, fraction: Float) {}
            override fun onPositionAnimationArgsChanged(args: ClockPositionAnimationArgs) {}
        }
    }
}
