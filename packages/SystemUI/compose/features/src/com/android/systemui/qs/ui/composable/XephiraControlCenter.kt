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
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.border
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

/**
 * Next-Gen Flagship Modular Liquid Glass Control Center (Android 17 / iOS 26 Inspired).
 *
 * Implements high-fluidity spring physics, dynamic radial glow blooms,
 * tactile haptic micro-ticks, dual vertical liquid cylinders, and interactive Bento grid modules.
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
    var dataActive by remember { mutableStateOf(true) }
    var bluetoothActive by remember { mutableStateOf(true) }
    var airplaneActive by remember { mutableStateOf(false) }

    var isPlaying by remember { mutableStateOf(true) }
    var mediaProgress by remember { mutableFloatStateOf(0.46f) }

    var flashlightActive by remember { mutableStateOf(false) }
    var dndActive by remember { mutableStateOf(false) }
    var rotateActive by remember { mutableStateOf(true) }
    var hotspotActive by remember { mutableStateOf(false) }
    var darkModeActive by remember { mutableStateOf(isDark) }
    var recordActive by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ─── 1. TOP DUAL-SEGMENT PILL SWITCHER & QUICK STATUS ───────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Segment Pill Switcher
            Row(
                modifier = Modifier
                    .pureLiquidGlass(
                        shape = RoundedCornerShape(20.dp),
                        cornerRadius = 20.dp,
                        refraction = 10f,
                        isDark = isDark
                    )
                    .padding(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Control Center Active Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isDark) Color(0x35FFFFFF) else Color(0xFF0F172A)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Control Center",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Notifications Switcher Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                            onSwitchToNotifications?.invoke()
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Notifications",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Color(0x99FFFFFF) else Color(0xFF64748B)
                    )
                }
            }

            // Status Bar Glance Pills (Battery + Settings)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Frosted Battery Pill
                Row(
                    modifier = Modifier
                        .pureLiquidGlass(
                            shape = RoundedCornerShape(14.dp),
                            cornerRadius = 14.dp,
                            refraction = 8f,
                            isDark = isDark
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(3.dp))
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

        // ─── 2. BENTO ROW 1: CONNECTIVITY HUB & MEDIA PLAYER ───────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 2x2 Connectivity Hub Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(172.dp)
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
                            subLabel = if (wifiActive) "Xephira_5G" else "Off",
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
                            subLabel = if (dataActive) "LTE+" else "Off",
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
                            subLabel = if (bluetoothActive) "Connected" else "Off",
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
                            subLabel = if (airplaneActive) "Active" else "Off",
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
                    .height(172.dp)
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
                    // Title & Album Art Thumbnail
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
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

                        // Mini album art glowing square
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF8B5CF6).copy(alpha = 0.35f),
                            border = BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.6f)),
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
                    BentoMiniWaveform(isPlaying = isPlaying, isDark = isDark)

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
                        // Play/Pause Morphing Button
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

        // ─── 4. BENTO ROW 3: CIRCULAR QUICK TOGGLES (6-ITEM MATRIX) ────────
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
                    label = "Rotate",
                    isActive = rotateActive,
                    glowColor = Color(0xFF06B6D4), // Cyan
                    isDark = isDark,
                    onClick = {
                        rotateActive = !rotateActive
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    }
                )

                CircularQuickToggle(
                    label = "Hotspot",
                    isActive = hotspotActive,
                    glowColor = Color(0xFF10B981), // Emerald
                    isDark = isDark,
                    onClick = {
                        hotspotActive = !hotspotActive
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    },
                    customIcon = {
                        Canvas(modifier = Modifier.size(20.dp)) {
                            val tint = if (hotspotActive) Color(0xFF10B981) else (if (isDark) Color.White else Color(0xFF0F172A))
                            val c = Offset(size.width / 2f, size.height * 0.72f)
                            // Central beacon dot
                            drawCircle(
                                color = tint,
                                radius = 2.4f,
                                center = c
                            )
                            // Inner signal wave
                            drawArc(
                                color = tint,
                                startAngle = 210f,
                                sweepAngle = 120f,
                                useCenter = false,
                                topLeft = Offset(c.x - 5.5f, c.y - 5.5f),
                                size = Size(11f, 11f),
                                style = Stroke(width = 1.6f)
                            )
                            // Outer signal wave
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

                CircularQuickToggle(
                    icon = Icons.Filled.DarkMode,
                    label = "Theme",
                    isActive = darkModeActive,
                    glowColor = Color(0xFF6366F1), // Indigo
                    isDark = isDark,
                    onClick = {
                        darkModeActive = !darkModeActive
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    }
                )

                CircularQuickToggle(
                    icon = Icons.Filled.Videocam,
                    label = "Record",
                    isActive = recordActive,
                    glowColor = Color(0xFFEF4444), // Crimson
                    isDark = isDark,
                    onClick = {
                        recordActive = !recordActive
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    }
                )
            }
        }

        // ─── 5. BOTTOM FROSTED DRAG HANDLE ─────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
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
 * Bento Toggle Icon with tactile spring press physics and radial glow aura.
 */
@Composable
private fun BentoToggleIcon(
    icon: ImageVector,
    label: String,
    subLabel: String = "",
    isActive: Boolean,
    activeColor: Color,
    isDark: Boolean,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "bento_press"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(52.dp)
        ) {
            // Dynamic Radial Glow Bloom
            if (isActive) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
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
                border = if (isActive) BorderStroke(1.2.dp, Color.White.copy(alpha = 0.7f)) else null,
                modifier = Modifier
                    .size(50.dp)
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
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isActive) Color.White else (if (isDark) Color.White else Color(0xFF0F172A)),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDark) Color.White else Color(0xFF0F172A)
        )
    }
}

/**
 * Circular Quick Toggle with tactile spring scaling, glowing stroke, and label.
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
            shape = CircleShape,
            color = if (isActive) glowColor.copy(alpha = 0.28f) else (if (isDark) Color(0x20FFFFFF) else Color(0x12000000)),
            border = if (isActive) BorderStroke(1.8.dp, glowColor) else BorderStroke(1.dp, if (isDark) Color(0x25FFFFFF) else Color(0x10000000)),
            modifier = Modifier
                .size(46.dp)
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
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDark) Color(0xB3FFFFFF) else Color(0xFF64748B)
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
