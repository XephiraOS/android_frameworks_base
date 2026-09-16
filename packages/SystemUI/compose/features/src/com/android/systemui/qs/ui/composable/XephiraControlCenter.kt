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
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HotspotStation
import androidx.compose.material.icons.filled.Nightlight
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.compose.modifiers.pureLiquidGlass
import com.android.systemui.volume.panel.component.volume.ui.composable.VerticalLiquidGlassSlider

/**
 * Next-Gen Modular Liquid Glass Control Center (Android 17 / iOS 26 Inspired).
 * Features a Bento Grid layout with Connectivity Hub, Media Player,
 * Dual Vertical Liquid Sliders, and Circular Glass Quick Toggles.
 */
@Composable
fun XephiraControlCenter(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    onOpenSettings: (() -> Unit)? = null
) {
    val view = LocalView.current

    var brightnessValue by remember { mutableFloatStateOf(0.72f) }
    var volumeValue by remember { mutableFloatStateOf(0.65f) }

    var wifiActive by remember { mutableStateOf(true) }
    var dataActive by remember { mutableStateOf(true) }
    var bluetoothActive by remember { mutableStateOf(true) }
    var airplaneActive by remember { mutableStateOf(false) }

    var flashlightActive by remember { mutableStateOf(false) }
    var dndActive by remember { mutableStateOf(false) }
    var rotateActive by remember { mutableStateOf(true) }
    var hotspotActive by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ─── 1. TOP HEADER & QUICK GLANCE BAR ──────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Control Center",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF0F172A),
                    letterSpacing = (-0.3).sp
                )
                Text(
                    text = "XephiraOS 1.0",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDark) Color(0x99FFFFFF) else Color(0xFF64748B)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = if (isDark) Color(0x22FFFFFF) else Color(0x14000000),
                    modifier = Modifier
                        .size(36.dp)
                        .clickable {
                            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                            onOpenSettings?.invoke()
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = if (isDark) Color.White else Color(0xFF0F172A),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // ─── 2. BENTO ROW 1: CONNECTIVITY HUB & MEDIA PLAYER ───────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 2x2 Connectivity Hub Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(168.dp)
                    .pureLiquidGlass(
                        shape = RoundedCornerShape(26.dp),
                        cornerRadius = 26.dp,
                        refraction = 14f,
                        isDark = isDark
                    )
                    .padding(14.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        BentoToggleIcon(
                            icon = Icons.Filled.Wifi,
                            label = "Wi-Fi",
                            isActive = wifiActive,
                            activeColor = Color(0xFF0EA5E9),
                            isDark = isDark,
                            onClick = {
                                wifiActive = !wifiActive
                                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                            }
                        )
                        BentoToggleIcon(
                            icon = Icons.Outlined.CellTower,
                            label = "5G Data",
                            isActive = dataActive,
                            activeColor = Color(0xFF10B981),
                            isDark = isDark,
                            onClick = {
                                dataActive = !dataActive
                                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        BentoToggleIcon(
                            icon = Icons.Outlined.Bluetooth,
                            label = "Bluetooth",
                            isActive = bluetoothActive,
                            activeColor = Color(0xFF8B5CF6),
                            isDark = isDark,
                            onClick = {
                                bluetoothActive = !bluetoothActive
                                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                            }
                        )
                        BentoToggleIcon(
                            icon = Icons.Outlined.AirplanemodeActive,
                            label = "Airplane",
                            isActive = airplaneActive,
                            activeColor = Color(0xFFF59E0B),
                            isDark = isDark,
                            onClick = {
                                airplaneActive = !airplaneActive
                                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                            }
                        )
                    }
                }
            }

            // 2x2 Now Playing Media Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(168.dp)
                    .pureLiquidGlass(
                        shape = RoundedCornerShape(26.dp),
                        cornerRadius = 26.dp,
                        refraction = 14f,
                        isDark = isDark
                    )
                    .padding(14.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Midnight Echo",
                                fontSize = 14.sp,
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

                        // Mini album art glowing square
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF8B5CF6).copy(alpha = 0.35f),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.GraphicEq,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Live Soundwave Equalizer Visualizer
                    BentoMiniWaveform(isDark = isDark)

                    // Playback Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FastRewind,
                            contentDescription = "Previous",
                            tint = if (isDark) Color.White else Color(0xFF0F172A),
                            modifier = Modifier.size(20.dp)
                        )
                        Surface(
                            shape = CircleShape,
                            color = if (isDark) Color.White else Color(0xFF0F172A),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Play",
                                    tint = if (isDark) Color(0xFF0F172A) else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Filled.FastForward,
                            contentDescription = "Next",
                            tint = if (isDark) Color.White else Color(0xFF0F172A),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // ─── 3. BENTO ROW 2: DUAL VERTICAL FLUID SLIDERS ───────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                // Brightness Vertical Tube
                VerticalLiquidGlassSlider(
                    value = brightnessValue,
                    onValueChange = { brightnessValue = it },
                    valueRange = 0f..1f,
                    icon = Icons.Filled.BrightnessMedium,
                    primaryColor = Color(0xFF38BDF8),
                    isDark = isDark
                )

                // Volume Vertical Tube
                VerticalLiquidGlassSlider(
                    value = volumeValue,
                    onValueChange = { volumeValue = it },
                    valueRange = 0f..1f,
                    icon = Icons.Filled.VolumeUp,
                    primaryColor = Color(0xFF8B5CF6),
                    isDark = isDark
                )
            }
        }

        // ─── 4. BENTO ROW 3: QUICK TOGGLE ACTION MATRIX ────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pureLiquidGlass(
                    shape = RoundedCornerShape(26.dp),
                    cornerRadius = 26.dp,
                    refraction = 14f,
                    isDark = isDark
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularQuickToggle(
                    icon = Icons.Filled.FlashlightOn,
                    label = "Flashlight",
                    isActive = flashlightActive,
                    glowColor = Color(0xFFFBBF24), // Amber
                    isDark = isDark,
                    onClick = {
                        flashlightActive = !flashlightActive
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    }
                )

                CircularQuickToggle(
                    icon = Icons.Filled.Nightlight,
                    label = "DND",
                    isActive = dndActive,
                    glowColor = Color(0xFFA855F7), // Purple
                    isDark = isDark,
                    onClick = {
                        dndActive = !dndActive
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    }
                )

                CircularQuickToggle(
                    icon = Icons.Filled.ScreenRotation,
                    label = "Auto-Rotate",
                    isActive = rotateActive,
                    glowColor = Color(0xFF06B6D4), // Cyan
                    isDark = isDark,
                    onClick = {
                        rotateActive = !rotateActive
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    }
                )

                CircularQuickToggle(
                    icon = Icons.Filled.HotspotStation,
                    label = "Hotspot",
                    isActive = hotspotActive,
                    glowColor = Color(0xFF10B981), // Emerald
                    isDark = isDark,
                    onClick = {
                        hotspotActive = !hotspotActive
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    }
                )

                CircularQuickToggle(
                    icon = Icons.Filled.Videocam,
                    label = "Record",
                    isActive = false,
                    glowColor = Color(0xFFEF4444), // Crimson
                    isDark = isDark,
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    }
                )
            }
        }
    }
}

@Composable
private fun BentoToggleIcon(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    activeColor: Color,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = if (isActive) activeColor else (if (isDark) Color(0x22FFFFFF) else Color(0x14000000)),
        modifier = Modifier
            .size(52.dp)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.White else (if (isDark) Color.White else Color(0xFF0F172A)),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun CircularQuickToggle(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    glowColor: Color,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = if (isActive) glowColor.copy(alpha = 0.28f) else (if (isDark) Color(0x20FFFFFF) else Color(0x12000000)),
            border = if (isActive) androidx.compose.foundation.BorderStroke(1.8.dp, glowColor) else null,
            modifier = Modifier
                .size(50.dp)
                .clickable { onClick() }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isActive) glowColor else (if (isDark) Color.White else Color(0xFF0F172A)),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDark) Color(0xB3FFFFFF) else Color(0xFF64748B)
        )
    }
}

@Composable
private fun BentoMiniWaveform(isDark: Boolean) {
    val transition = rememberInfiniteTransition(label = "bento_waveform")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
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
            val sinVal = Math.sin((i.toDouble() * 0.45) + phase).toFloat()
            val barHeight = ((sinVal + 1f) * 0.5f * size.height * 0.85f).coerceAtLeast(4f)
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
