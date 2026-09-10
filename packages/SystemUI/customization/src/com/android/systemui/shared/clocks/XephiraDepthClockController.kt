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
 * Fully compliant with Android 16 (LineageOS 23.2) ClockController architecture.
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
            smallClock.events.onTimeTick()
            largeClock.events.onTimeTick()
        }

        override fun onTimeFormatChanged(formatKind: TimeFormatKind) {
            is24HourFormat = formatKind == TimeFormatKind.FULL_DAY
            timeFormat.applyPattern(if (is24HourFormat) "HH:mm" else "hh:mm")
            smallClock.events.onTimeTick()
            largeClock.events.onTimeTick()
        }

        override fun onLocaleChanged(locale: Locale) {
            timeFormat.setCalendar(Calendar.getInstance(timeFormat.timeZone, locale))
            smallClock.events.onTimeTick()
            largeClock.events.onTimeTick()
        }

        override fun onAlarmDataChanged(alarmData: AlarmData) {}
        override fun onWeatherDataChanged(weatherData: WeatherData) {}
        override fun onZenDataChanged(zenData: ZenData) {}
    }

    init {
        smallClock.events.onTimeTick()
        largeClock.events.onTimeTick()
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
        tv.setSingleLine(true)
        tv.visibility = View.VISIBLE
        tv.alpha = 1f
        return tv
    }

    private fun renderFace(face: XephiraClockFaceController) {
        val now = Date()
        val formatted = timeFormat.format(now)
        val tv = face.view

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
            tv.setTextColor(targetColor)
            tv.text = span
        } else if (clockVariant == "XEPHIRA_AETHER_SPATIAL") {
            // Cyan tinted numerals with soft spatial depth shadow
            tv.setTextColor(Color.parseColor("#00F2FE"))
            if (face.isLarge) {
                tv.setShadowLayer(16f, 0f, 4f, Color.parseColor("#6600F2FE"))
            } else {
                tv.setShadowLayer(8f, 0f, 2f, Color.parseColor("#6600F2FE"))
            }
            tv.text = formatted
        } else {
            // iOS 18 Bold Depth Clock
            tv.setTextColor(targetColor)
            tv.typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
            tv.text = formatted
        }
    }

    inner class XephiraClockFaceController(
        override val view: TextView,
        val isLarge: Boolean
    ) : ClockFaceController {
        override val config = ClockFaceConfig()
        override var theme: ThemeConfig = ThemeConfig(isDarkTheme = true, settings.seedColor)
        override val layout = DefaultClockFaceLayout(view)

        override val events = object : ClockFaceEvents {
            override fun onTimeTick() {
                renderFace(this@XephiraClockFaceController)
            }
            override fun onThemeChanged(theme: ThemeConfig) {
                this@XephiraClockFaceController.theme = theme
                val defaultColor = theme.getDefaultColor(ctx)
                targetColor = settings.seedColor ?: defaultColor
                renderFace(this@XephiraClockFaceController)
            }
            override fun onFontSettingChanged(fontSizePx: Float) {
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSizePx)
            }
            override fun onTargetRegionChanged(targetRegion: Rect?) {}
            override fun onSecondaryDisplayChanged(onSecondaryDisplay: Boolean) {}
        }

        override var animations: ClockAnimations = object : ClockAnimations {
            override fun enter() {}
            override fun doze(fraction: Float) {
                view.alpha = 1.0f - (fraction * 0.4f)
            }
            override fun fold(fraction: Float) {}
            override fun charge() {}
            override fun onPickerCarouselSwiping(swipingFraction: Float) {
                view.translationY = 0.5f * view.bottom * (1 - swipingFraction)
            }
            override fun onPositionAnimated(args: ClockPositionAnimationArgs) {}
            override fun onFidgetTap(x: Float, y: Float) {}
            override fun onFontAxesChanged(style: ClockAxisStyle) {}
        }
    }
}
