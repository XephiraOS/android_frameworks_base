/*
 * Copyright (C) 2026 XephiraOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.android.systemui.shared.clocks

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.android.systemui.customization.clocks.DefaultClockFaceLayout
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
import com.android.systemui.plugins.keyguard.ui.clocks.ClockViewIds
import com.android.systemui.plugins.keyguard.ui.clocks.ThemeConfig
import com.android.systemui.plugins.keyguard.ui.clocks.TimeFormatKind
import java.io.PrintWriter
import java.util.Date
import java.util.Locale

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
    private var is24HourFormat = false
    private val timeFormat = SimpleDateFormat("hh:mm", Locale.getDefault())

    override val config = ClockConfig(
        id = settings.clockId ?: clockVariant,
        name = when (clockVariant) {
            "XEPHIRA_ONEPLUS_RED1" -> "OnePlus Crimson 1"
            "XEPHIRA_AETHER_SPATIAL" -> "Aether Spatial Glass"
            else -> "iOS 18 Depth Clock"
        },
        description = "Xephira flagship spatial depth typography"
    )

    private val smallClockView = createClockTextView(isLarge = false).apply {
        id = ClockViewIds.LOCKSCREEN_CLOCK_VIEW_SMALL
    }
    private val largeClockView = createClockTextView(isLarge = true).apply {
        id = ClockViewIds.LOCKSCREEN_CLOCK_VIEW_LARGE
    }

    override val smallClock = XephiraClockFaceController(smallClockView, isLarge = false)
    override val largeClock = XephiraClockFaceController(largeClockView, isLarge = true)

    override val eventListeners = ClockEventListeners()

    override val events = object : ClockEvents {
        override var isReactiveTouchInteractionEnabled: Boolean = false

        override fun onTimeZoneChanged(timeZone: TimeZone) {
            timeFormat.timeZone = timeZone
            refreshTime()
        }

        override fun onTimeFormatChanged(formatKind: TimeFormatKind) {
            is24HourFormat = formatKind == TimeFormatKind.FULL_DAY
            timeFormat.applyPattern(if (is24HourFormat) "HH:mm" else "hh:mm")
            refreshTime()
        }

        override fun onLocaleChanged(locale: Locale) {
            timeFormat.setCalendar(Calendar.getInstance(timeFormat.timeZone, locale))
            refreshTime()
        }

        override fun onAlarmDataChanged(alarmData: AlarmData) {}
        override fun onWeatherDataChanged(weatherData: WeatherData) {}
        override fun onZenDataChanged(zenData: ZenData) {}
    }

    init {
        refreshTime()
    }

    override fun initialize(isDarkTheme: Boolean, dozeFraction: Float, foldFraction: Float) {
        smallClock.run {
            events.onThemeChanged(theme.copy(isDarkTheme = isDarkTheme))
            animations.doze(dozeFraction)
            animations.fold(foldFraction)
            events.onTimeTick()
        }
        largeClock.run {
            events.onThemeChanged(theme.copy(isDarkTheme = isDarkTheme))
            animations.doze(dozeFraction)
            animations.fold(foldFraction)
            events.onTimeTick()
        }
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
        override var theme: ThemeConfig = ThemeConfig(isDarkTheme = true, settings.seedColor)
        override val layout = DefaultClockFaceLayout(view)

        override val events = object : ClockFaceEvents {
            override fun onTimeTick() {
                refreshTime()
            }
            override fun onThemeChanged(theme: ThemeConfig) {
                this@XephiraClockFaceController.theme = theme
                val defaultColor = theme.getDefaultColor(ctx)
                targetColor = settings.seedColor ?: defaultColor
                clockView.setTextColor(targetColor)
                refreshTime()
            }
            override fun onFontSettingChanged(fontSizePx: Float) {
                clockView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSizePx)
            }
            override fun onTargetRegionChanged(targetRegion: Rect?) {}
            override fun onSecondaryDisplayChanged(onSecondaryDisplay: Boolean) {}
        }

        override var animations: ClockAnimations = object : ClockAnimations {
            override fun enter() {}
            override fun doze(fraction: Float) {
                clockView.alpha = 1.0f - (fraction * 0.4f)
            }
            override fun fold(fraction: Float) {}
            override fun charge() {}
            override fun onPickerCarouselSwiping(swipingFraction: Float) {}
            override fun onPositionAnimated(args: ClockPositionAnimationArgs) {}
            override fun onFidgetTap(x: Float, y: Float) {}
            override fun onFontAxesChanged(style: ClockAxisStyle) {}
        }
    }
}
