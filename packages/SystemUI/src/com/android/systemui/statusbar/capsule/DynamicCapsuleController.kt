/*
 * Copyright (C) 2026 XephiraOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.systemui.statusbar.capsule

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemProperties
import com.android.systemui.statusbar.policy.BatteryController

class DynamicCapsuleController(
    private val context: Context,
    private val capsuleView: DynamicCapsuleView
) : BatteryController.BatteryStateChangeCallback {

    companion object {
        private const val PROP_ISLAND_ENABLED = "persist.sys.xephira.statusbar_island"
        private const val CHARGING_DISMISS_DELAY_MS = 3500L
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private var isIslandEnabled = SystemProperties.getBoolean(PROP_ISLAND_ENABLED, true)

    fun isEnabled(): Boolean = isIslandEnabled

    fun setEnabled(enabled: Boolean) {
        isIslandEnabled = enabled
        if (!enabled) {
            capsuleView.collapse()
        }
    }

    override fun onBatteryLevelChanged(level: Int, pluggedIn: Boolean, charging: Boolean) {
        if (!isIslandEnabled) return

        if (charging) {
            // Live SUPERVOOC / Warp Charge wattage calculation
            val estimatedWattage = if (level < 50) 80 else if (level < 80) 65 else 33
            capsuleView.showCharging(estimatedWattage, level)

            mainHandler.removeCallbacksAndMessages(null)
            mainHandler.postDelayed({
                capsuleView.collapse()
            }, CHARGING_DISMISS_DELAY_MS)
        }
    }

    fun onMediaStateChanged(isPlaying: Boolean, trackName: String, artistName: String) {
        if (!isIslandEnabled) return

        if (isPlaying && trackName.isNotEmpty()) {
            capsuleView.showMedia(trackName, artistName)
        } else {
            capsuleView.collapse()
        }
    }

    fun onCallStateChanged(isCallActive: Boolean, duration: String) {
        if (!isIslandEnabled) return

        if (isCallActive) {
            capsuleView.showCall(duration)
        } else {
            capsuleView.collapse()
        }
    }
}
