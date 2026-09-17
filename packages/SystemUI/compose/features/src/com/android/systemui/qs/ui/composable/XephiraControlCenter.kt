/*
 * Copyright (C) 2026 XephiraOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.qs.ui.composable

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.outlined.AirplanemodeActive
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.CellTower
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.compose.modifiers.pureLiquidGlass
import com.android.systemui.volume.panel.component.volume.ui.composable.VerticalLiquidGlassSlider
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Next-Gen Flagship Modular Liquid Glass Control Center.
 *
 * Implements high-fluidity spring physics, dynamic radial glow blooms,
 * tactile haptic micro-ticks, dual vertical liquid cylinders, squircle Bento quick toggles,
 * and interactive media equalizer card.
 */
@Composable
fun XephiraControlCenter(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    onOpenSettings: (() -> Unit)? = null,
    onSwitchToNotifications: (() -> Unit)? = null
) {
    val view = LocalView.current

    var brightnessValue by remember { mutableFloatStateOf(0.72f) }
    var volumeValue by remember { mutableFloatStateOf(0.65f) }

    var wifiActive by remember { mutableStateOf(true) }
    var bluetoothActive by remember { mutableStateOf(true) }
    var airplaneActive by remember { mutableStateOf(false) }

    var isPlaying by remember { mutableStateOf(true) }
    var mediaProgress by remember { mutableFloatStateOf(0.46f) }

    // Center Matrix 2x4 States
    var flashlightActive by remember { mutableStateOf(false) }
    var rotateActive by remember { mutableStateOf(true) }
    var hotspotActive by remember { mutableStateOf(false) }
    var nightModeActive by remember { mutableStateOf(false) }
    var dndActive by remember { mutableStateOf(false) }
    var muteActive by remember { mutableStateOf(false) }
    var batterySaverActive by remember { mutableStateOf(false) }

    val currentTime = remember {
        try {
            val format = SimpleDateFormat("h:mm a", Locale.getDefault())
            format.format(Date())
        } catch (_: Exception) {
            "10:14 PM"
        }
    }

    val currentDate = remember {
        try {
            val format = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
            format.format(Date()).uppercase()
        } catch (_: Exception) {
            "TUE, OCT 26"
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ─── 1. TOP HEADER: DIGITAL CLOCK & GLANCE PILLS ─────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clock & Date Stack
            Column {
                Text(
                    text = currentTime,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF0F172A),
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = currentDate,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color(0xCCFFFFFF) else Color(0xFF64748B),
                    letterSpacing = 0.5.sp
                )
            }

            // Status Bar Glance Pills (Switcher + Battery + Settings)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Segment Pill Switcher
                Row(
                    modifier = Modifier
                        .pureLiquidGlass(
                            shape = RoundedCornerShape(18.dp),
                            cornerRadius = 18.dp,
                            refraction = 10f,
                            isDark = isDark
                        )
                        .padding(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isDark) Color(0x35FFFFFF) else Color(0xFF0F172A))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Control",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                                onSwitchToNotifications?.invoke()
                            }
                            .padding(horizontal = 9.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Notifs",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDark) Color(0x99FFFFFF) else Color(0xFF64748B)
                        )
                    }
                }

                // Frosted Battery Pill
                Row(
                    modifier = Modifier
                        .pureLiquidGlass(
                            shape = RoundedCornerShape(14.dp),
                            cornerRadius = 14.dp,
                            refraction = 8f,
                            isDark = isDark
                        )
                        .padding(horizontal = 9.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(14.dp)
                            .height(7.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFF10B981))
                    )
                    Text(
                        text = "88%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                }

                // Settings Button
                LiquidIconButton(
                    icon = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    isDark = isDark,
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        onOpenSettings?.invoke()
                    }
                )
            }
        }

        // ─── 2. BENTO ROW 1: CONNECTIVITY (LEFT) & VERTICAL SLIDERS (RIGHT) ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Column: Wi-Fi & Bluetooth Stacked Bento Cards
            Column(
                modifier = Modifier.weight(1.05f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BentoConnectivityCard(
                    title = "Wi-Fi",
                    subtitle = if (wifiActive) "Xephira_5G" else "Off",
                    icon = Icons.Filled.Wifi,
                    isActive = wifiActive,
                    activeColor = Color(0xFF0EA5E9),
                    isDark = isDark,
                    onClick = {
                        wifiActive = !wifiActive
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    }
                )

                BentoConnectivityCard(
                    title = "Bluetooth",
                    subtitle = if (bluetoothActive) "Connected" else "Off",
                    icon = Icons.Outlined.Bluetooth,
                    isActive = bluetoothActive,
                    activeColor = Color(0xFF8B5CF6),
                    isDark = isDark,
                    onClick = {
                        bluetoothActive = !bluetoothActive
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    }
                )
            }

            // Right Column: Dual Vertical Liquid Glass Sliders
            Row(
                modifier = Modifier.weight(0.95f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brightness Slider
                VerticalLiquidGlassSlider(
                    value = brightnessValue,
                    onValueChange = { brightnessValue = it },
                    valueRange = 0f..1f,
                    icon = Icons.Filled.BrightnessMedium,
                    primaryColor = Color(0xFF38BDF8),
                    isDark = isDark,
                    sliderWidth = 68.dp,
                    sliderHeight = 170.dp,
                    modifier = Modifier.weight(1f)
                )

                // Volume Slider
                VerticalLiquidGlassSlider(
                    value = volumeValue,
                    onValueChange = { volumeValue = it },
                    valueRange = 0f..1f,
                    icon = Icons.Filled.VolumeUp,
                    primaryColor = Color(0xFF8B5CF6),
                    isDark = isDark,
                    sliderWidth = 68.dp,
                    sliderHeight = 170.dp,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ─── 3. BENTO ROW 2: CENTER MATRIX (2x4 SQUIRCLE QUICK TOGGLES) ─────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pureLiquidGlass(
                    shape = RoundedCornerShape(26.dp),
                    cornerRadius = 26.dp,
                    refraction = 14f,
                    isDark = isDark
                )
                .padding(horizontal = 10.dp, vertical = 14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Row 1 (4 Toggles)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularQuickToggle(
                        icon = Icons.Filled.FlashlightOn,
                        label = "Flashlight",
                        isActive = flashlightActive,
                        glowColor = Color(0xFFFBBF24),
                        isDark = isDark,
                        onClick = {
                            flashlightActive = !flashlightActive
                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                        }
                    )

                    CircularQuickToggle(
                        icon = Icons.Outlined.AirplanemodeActive,
                        label = "Airplane",
                        isActive = airplaneActive,
                        glowColor = Color(0xFFF59E0B),
                        isDark = isDark,
                        onClick = {
                            airplaneActive = !airplaneActive
                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                        }
                    )

                    CircularQuickToggle(
                        icon = Icons.Filled.ScreenRotation,
                        label = "Rotate",
                        isActive = rotateActive,
                        glowColor = Color(0xFF06B6D4),
                        isDark = isDark,
                        onClick = {
                            rotateActive = !rotateActive
                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                        }
                    )

                    CircularQuickToggle(
                        label = "Hotspot",
                        isActive = hotspotActive,
                        glowColor = Color(0xFF10B981),
                        isDark = isDark,
                        onClick = {
                            hotspotActive = !hotspotActive
                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                        },
                        customIcon = {
                            Canvas(modifier = Modifier.size(20.dp)) {
                                val tint = if (hotspotActive) Color(0xFF10B981) else (if (isDark) Color.White else Color(0xFF0F172A))
                                val c = Offset(size.width / 2f, size.height * 0.72f)
                                drawCircle(color = tint, radius = 2.4f, center = c)
                                drawArc(
                                    color = tint,
                                    startAngle = 210f,
                                    sweepAngle = 120f,
                                    useCenter = false,
                                    topLeft = Offset(c.x - 5.5f, c.y - 5.5f),
                                    size = Size(11f, 11f),
                                    style = Stroke(width = 1.6f)
                                )
                                drawArc(
                                    color = tint,
                                    startAngle = 205f,
                                    sweepAngle = 130f,
                                    useCenter = false,
                                    topLeft = Offset(c.x - 9.5f, c.y - 9.5f),
                                    size = Size(19f, 19f),
                                    style = Stroke(width = 1.6f)
                                )
                            }
                        }
                    )
                }

                // Row 2 (4 Toggles)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularQuickToggle(
                        icon = Icons.Filled.Nightlight,
                        label = "Night Light",
                        isActive = nightModeActive,
                        glowColor = Color(0xFF6366F1),
                        isDark = isDark,
                        onClick = {
                            nightModeActive = !nightModeActive
                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                        }
                    )

                    CircularQuickToggle(
                        label = "DND",
                        isActive = dndActive,
                        glowColor = Color(0xFFA855F7),
                        isDark = isDark,
                        onClick = {
                            dndActive = !dndActive
                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                        },
                        customIcon = {
                            Canvas(modifier = Modifier.size(20.dp)) {
                                val tint = if (dndActive) Color(0xFFA855F7) else (if (isDark) Color.White else Color(0xFF0F172A))
                                drawCircle(color = tint, radius = size.minDimension / 2f - 1.5f, style = Stroke(width = 1.8f))
                                drawLine(
                                    color = tint,
                                    start = Offset(size.width * 0.28f, size.height * 0.5f),
                                    end = Offset(size.width * 0.72f, size.height * 0.5f),
                                    strokeWidth = 2.2f
                                )
                            }
                        }
                    )

                    CircularQuickToggle(
                        label = "Mute",
                        isActive = muteActive,
                        glowColor = Color(0xFFF43F5E),
                        isDark = isDark,
                        onClick = {
                            muteActive = !muteActive
                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                        },
                        customIcon = {
                            Canvas(modifier = Modifier.size(20.dp)) {
                                val tint = if (muteActive) Color(0xFFF43F5E) else (if (isDark) Color.White else Color(0xFF0F172A))
                                val p = Path().apply {
                                    moveTo(size.width * 0.2f, size.height * 0.35f)
                                    lineTo(size.width * 0.38f, size.height * 0.35f)
                                    lineTo(size.width * 0.62f, size.height * 0.18f)
                                    lineTo(size.width * 0.62f, size.height * 0.82f)
                                    lineTo(size.width * 0.38f, size.height * 0.65f)
                                    lineTo(size.width * 0.2f, size.height * 0.65f)
                                    close()
                                }
                                drawPath(p, color = tint, style = Stroke(width = 1.6f))
                                if (muteActive) {
                                    drawLine(
                                        color = tint,
                                        start = Offset(size.width * 0.7f, size.height * 0.35f),
                                        end = Offset(size.width * 0.88f, size.height * 0.65f),
                                        strokeWidth = 2f
                                    )
                                    drawLine(
                                        color = tint,
                                        start = Offset(size.width * 0.88f, size.height * 0.35f),
                                        end = Offset(size.width * 0.7f, size.height * 0.65f),
                                        strokeWidth = 2f
                                    )
                                }
                            }
                        }
                    )

                    CircularQuickToggle(
                        label = "Saver",
                        isActive = batterySaverActive,
                        glowColor = Color(0xFF22C55E),
                        isDark = isDark,
                        onClick = {
                            batterySaverActive = !batterySaverActive
                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                        },
                        customIcon = {
                            Canvas(modifier = Modifier.size(20.dp)) {
                                val tint = if (batterySaverActive) Color(0xFF22C55E) else (if (isDark) Color.White else Color(0xFF0F172A))
                                val bodyW = size.width * 0.52f
                                val bodyH = size.height * 0.72f
                                val left = (size.width - bodyW) / 2f
                                val top = (size.height - bodyH) / 2f + 1f
                                drawRoundRect(
                                    color = tint,
                                    topLeft = Offset(left, top),
                                    size = Size(bodyW, bodyH),
                                    cornerRadius = CornerRadius(2.5f, 2.5f),
                                    style = Stroke(width = 1.6f)
                                )
                                drawRoundRect(
                                    color = tint,
                                    topLeft = Offset(size.width * 0.42f, top - 2.5f),
                                    size = Size(size.width * 0.16f, 2.5f),
                                    cornerRadius = CornerRadius(1f, 1f)
                                )
                                if (batterySaverActive) {
                                    drawLine(color = tint, start = Offset(size.width * 0.5f, top + 3f), end = Offset(size.width * 0.5f, top + bodyH - 3f), strokeWidth = 1.6f)
                                    drawLine(color = tint, start = Offset(left + 3f, top + bodyH * 0.5f), end = Offset(left + bodyW - 3f, top + bodyH * 0.5f), strokeWidth = 1.6f)
                                }
                            }
                        }
                    )
                }
            }
        }

        // ─── 4. BENTO ROW 3: FROSTED MEDIA PLAYER CARD ───────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pureLiquidGlass(
                    shape = RoundedCornerShape(26.dp),
                    cornerRadius = 26.dp,
                    refraction = 14f,
                    isDark = isDark
                )
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Track Info & Mini Waveform Equalizer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF8B5CF6).copy(alpha = 0.35f),
                            border = BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.6f)),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.GraphicEq,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "Midnight Echo",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF0F172A),
                                maxLines = 1
                            )
                            Text(
                                text = "Stellaris",
                                fontSize = 11.sp,
                                color = if (isDark) Color(0x99FFFFFF) else Color(0xFF64748B),
                                maxLines = 1
                            )
                        }
                    }

                    // Live Soundwave Equalizer
                    Box(modifier = Modifier.width(90.dp)) {
                        BentoMiniWaveform(isPlaying = isPlaying, isDark = isDark)
                    }
                }

                // Timeline Scrubber Bar
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isDark) Color(0x22FFFFFF) else Color(0x14000000))
                            .clickable {
                                mediaProgress = (mediaProgress + 0.15f) % 1f
                                view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(mediaProgress)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF38BDF8), Color(0xFF8B5CF6))
                                    )
                                )
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "1:42", fontSize = 9.sp, color = if (isDark) Color(0x66FFFFFF) else Color(0xFF94A3B8))
                        Text(text = "3:30", fontSize = 9.sp, color = if (isDark) Color(0x66FFFFFF) else Color(0xFF94A3B8))
                    }
                }

                // Playback Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LiquidPressableIcon(
                        icon = Icons.Filled.FastRewind,
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        },
                        tint = if (isDark) Color.White else Color(0xFF0F172A),
                        size = 20.dp
                    )

                    // Play/Pause Circular Morphing Pill
                    Surface(
                        shape = CircleShape,
                        color = if (isDark) Color.White else Color(0xFF0F172A),
                        modifier = Modifier
                            .size(36.dp)
                            .clickable {
                                isPlaying = !isPlaying
                                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = if (isDark) Color(0xFF0F172A) else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    LiquidPressableIcon(
                        icon = Icons.Filled.FastForward,
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        },
                        tint = if (isDark) Color.White else Color(0xFF0F172A),
                        size = 20.dp
                    )
                }
            }
        }

        // ─── 5. BOTTOM FROSTED DRAG HANDLE ───────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (isDark) Color(0x40FFFFFF) else Color(0x30000000))
            )
        }
    }
}

/**
 * Bento Connectivity Card with glowing aura and tactile spring response.
 */
@Composable
private fun BentoConnectivityCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isActive: Boolean,
    activeColor: Color,
    isDark: Boolean,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "bento_card_press"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pureLiquidGlass(
                shape = RoundedCornerShape(22.dp),
                cornerRadius = 22.dp,
                refraction = 12f,
                isDark = isDark
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(46.dp)
            ) {
                if (isActive) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(activeColor.copy(alpha = 0.5f), Color.Transparent)
                                )
                            )
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = if (isActive) activeColor else (if (isDark) Color(0x22FFFFFF) else Color(0x14000000)),
                    border = if (isActive) BorderStroke(1.2.dp, Color.White.copy(alpha = 0.8f)) else BorderStroke(1.dp, Color(0x20FFFFFF)),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = if (isActive) Color.White else (if (isDark) Color.White else Color(0xFF0F172A)),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF0F172A),
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isActive) activeColor else (if (isDark) Color(0x99FFFFFF) else Color(0xFF64748B)),
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Squircle Quick Toggle with tactile spring scaling, specular border, and label.
 */
@Composable
private fun CircularQuickToggle(
    label: String,
    isActive: Boolean,
    glowColor: Color,
    isDark: Boolean,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    customIcon: (@Composable () -> Unit)? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "quick_press"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = if (isActive) glowColor.copy(alpha = 0.28f) else (if (isDark) Color(0x20FFFFFF) else Color(0x12000000)),
            border = if (isActive) BorderStroke(1.6.dp, glowColor) else BorderStroke(1.dp, if (isDark) Color(0x25FFFFFF) else Color(0x10000000)),
            modifier = Modifier
                .size(48.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        },
                        onTap = { onClick() }
                    )
                }
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (customIcon != null) {
                    customIcon()
                } else if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isActive) glowColor else (if (isDark) Color.White else Color(0xFF0F172A)),
                        modifier = Modifier.size(21.dp)
                    )
                }
            }
        }

        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isActive) (if (isDark) Color.White else Color(0xFF0F172A)) else (if (isDark) Color(0xB3FFFFFF) else Color(0xFF64748B))
        )
    }
}

/**
 * Interactive pressable icon with spring bounce.
 */
@Composable
private fun LiquidPressableIcon(
    icon: ImageVector,
    onClick: () -> Unit,
    tint: Color,
    size: androidx.compose.ui.unit.Dp
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 800f),
        label = "icon_press"
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            }
            .padding(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(size)
        )
    }
}

/**
 * Top icon button with liquid glass styling.
 */
@Composable
private fun LiquidIconButton(
    icon: ImageVector,
    contentDescription: String,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = if (isDark) Color(0x22FFFFFF) else Color(0x14000000),
        border = BorderStroke(1.dp, if (isDark) Color(0x35FFFFFF) else Color(0x15000000)),
        modifier = Modifier
            .size(34.dp)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (isDark) Color.White else Color(0xFF0F172A),
                modifier = Modifier.size(17.dp)
            )
        }
    }
}

/**
 * Live soundwave visualizer that pauses when playback is paused.
 */
@Composable
private fun BentoMiniWaveform(isPlaying: Boolean, isDark: Boolean) {
    val transition = rememberInfiniteTransition(label = "bento_waveform")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phase"
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
    ) {
        val barCount = 18
        val spacing = size.width / barCount
        val barWidth = 3.5f

        for (i in 0 until barCount) {
            val baseSin = if (isPlaying) Math.sin((i.toDouble() * 0.45) + phase).toFloat() else 0.2f
            val barHeight = ((baseSin + 1f) * 0.5f * size.height * 0.85f).coerceAtLeast(4f)
            val x = i * spacing + (spacing - barWidth) / 2f
            val y = (size.height - barHeight) / 2f

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF38BDF8),
                        Color(0xFF8B5CF6)
                    )
                ),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(2f, 2f)
            )
        }
    }
}
