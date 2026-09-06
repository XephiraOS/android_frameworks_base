/*
 * Copyright (C) 2026 XephiraOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.systemui.wallpapers

import android.content.Context
import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.android.systemui.statusbar.policy.KeyguardStateController
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Controller responsible for the Apple iOS 18-style Neural Depth Wallpaper Engine.
 * 
 * Compositing Order:
 *   Layer 0: Background Wallpaper (System WallpaperManager)
 *   Layer 1: Lockscreen Clock & Typography (KeyguardClockSwitch)
 *   Layer 2: Foreground Subject Mask (Managed by this Controller)
 *   Layer 3: Lockscreen Widgets, Notifications & Biometric Prompts
 */
@Singleton
class DepthWallpaperController @Inject constructor(
    private val context: Context,
    private val keyguardStateController: KeyguardStateController
) {
    companion object {
        private const val TAG = "DepthWallpaperController"
        const val SETTING_DEPTH_ENABLED = "lockscreen_depth_wallpaper_enabled"
        const val SETTING_DEPTH_PATH = "lockscreen_depth_wallpaper_path"
        const val SETTING_OCCLUSION_LIMIT = "lockscreen_depth_occlusion_limit"
        
        const val DEFAULT_EXTRAS_PATH = "/product/media/wallpapers/depth/wallpaper_01_aether"
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private var isDepthEnabled = false
    private var activeMaskPath: String? = null
    private var cachedMaskBitmap: Bitmap? = null
    private var subjectMaskView: ImageView? = null
    private var maxOcclusionPercent = 30

    private val settingsObserver = object : ContentObserver(mainHandler) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            reloadSettings()
            updateMaskView()
        }
    }

    init {
        context.contentResolver.registerContentObserver(
            Settings.Secure.getUriFor(SETTING_DEPTH_ENABLED),
            false,
            settingsObserver
        )
        context.contentResolver.registerContentObserver(
            Settings.Secure.getUriFor(SETTING_DEPTH_PATH),
            false,
            settingsObserver
        )
        context.contentResolver.registerContentObserver(
            Settings.Secure.getUriFor(SETTING_OCCLUSION_LIMIT),
            false,
            settingsObserver
        )
        reloadSettings()
    }

    /**
     * Attaches the foreground subject mask view to the lockscreen layout hierarchy.
     */
    fun attachSubjectMaskView(view: ImageView) {
        subjectMaskView = view
        view.scaleType = ImageView.ScaleType.CENTER_CROP
        updateMaskView()
    }

    /**
     * Detaches the subject mask view when keyguard view is destroyed.
     */
    fun detachSubjectMaskView() {
        subjectMaskView?.setImageDrawable(null)
        subjectMaskView = null
    }

    /**
     * Dynamically adjusts the subject mask opacity based on notification shade drag fraction
     * and unlock progression to ensure zero visual obstruction of incoming notifications.
     */
    fun onShadeExpansionChanged(expansionFraction: Float) {
        val targetView = subjectMaskView ?: return
        if (!isDepthEnabled || cachedMaskBitmap == null) {
            targetView.visibility = View.GONE
            return
        }

        // As notifications expand or user scrolls, smoothly fade out the foreground mask
        val alpha = (1.0f - (expansionFraction * 1.5f)).coerceIn(0.0f, 1.0f)
        targetView.alpha = alpha
        targetView.visibility = if (alpha > 0.01f) View.VISIBLE else View.GONE
    }

    private fun reloadSettings() {
        isDepthEnabled = Settings.Secure.getInt(
            context.contentResolver,
            SETTING_DEPTH_ENABLED,
            1
        ) == 1

        activeMaskPath = Settings.Secure.getString(
            context.contentResolver,
            SETTING_DEPTH_PATH
        ) ?: DEFAULT_EXTRAS_PATH

        maxOcclusionPercent = Settings.Secure.getInt(
            context.contentResolver,
            SETTING_OCCLUSION_LIMIT,
            30
        )

        loadMaskBitmapAsync()
    }

    private fun loadMaskBitmapAsync() {
        val basePath = activeMaskPath ?: return
        Thread {
            try {
                val maskFile = File(basePath, "subject_mask.png")
                if (maskFile.exists() && maskFile.canRead()) {
                    val bitmap = BitmapFactory.decodeFile(maskFile.absolutePath)
                    mainHandler.post {
                        cachedMaskBitmap = bitmap
                        updateMaskView()
                    }
                } else {
                    Log.d(TAG, "No subject_mask.png found at: $basePath")
                    mainHandler.post {
                        cachedMaskBitmap = null
                        updateMaskView()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading depth wallpaper mask", e)
            }
        }.start()
    }

    private fun updateMaskView() {
        val view = subjectMaskView ?: return
        if (isDepthEnabled && cachedMaskBitmap != null && keyguardStateController.isShowing) {
            view.setImageBitmap(cachedMaskBitmap)
            view.visibility = View.VISIBLE
            view.alpha = 1.0f
        } else {
            view.setImageDrawable(null)
            view.visibility = View.GONE
        }
    }
}
