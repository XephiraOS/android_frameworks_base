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

package com.android.systemui.volume.panel.component.volume.ui.composable

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.compose.modifiers.pureLiquidGlass

/**
 * Next-Gen Vertical Liquid Glass Cylinder Slider.
 *
 * Simulates a vertical fluid tube with animated liquid fill, meniscus curvature,
 * physical drag inertia, specular crest reflections, and continuous haptic ticks.
 */
@Composable
fun VerticalLiquidGlassSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    icon: ImageVector? = null,
    primaryColor: Color = Color(0xFF38BDF8),
    isDark: Boolean = isSystemInDarkTheme()
) {
    val view = LocalView.current
    var heightPx by remember { mutableFloatStateOf(1f) }
    var isDragging by remember { mutableStateOf(false) }
    var lastHapticStep by remember { mutableFloatStateOf(-1f) }

    val normalized = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start))
        .coerceIn(0f, 1f)

    val animatedNormalized by animateFloatAsState(
        targetValue = normalized,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 650f),
        label = "vertical_fluid_fill"
    )

    val dragTilt by animateFloatAsState(
        targetValue = if (isDragging) 6f else 0f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "vertical_drag_tilt"
    )

    val pressScale by animateFloatAsState(
        targetValue = if (isDragging) 0.96f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 600f),
        label = "vertical_press_scale"
    )

    val hudAlpha by animateFloatAsState(
        targetValue = if (isDragging) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 700f),
        label = "hud_alpha"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .width(82.dp)
            .height(200.dp)
            .pureLiquidGlass(
                shape = RoundedCornerShape(36.dp),
                cornerRadius = 36.dp,
                refraction = 14f,
                isDark = isDark
            )
            .clip(RoundedCornerShape(36.dp))
            .onSizeChanged { heightPx = it.height.toFloat().coerceAtLeast(1f) }
            .pointerInput(valueRange) {
                detectTapGestures { offset ->
                    val newNorm = (1f - (offset.y / heightPx)).coerceIn(0f, 1f)
                    val newValue = valueRange.start + newNorm * (valueRange.endInclusive - valueRange.start)
                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                    onValueChange(newValue)
                }
            }
            .pointerInput(valueRange) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        view.performHapticFeedback(HapticFeedbackConstants.GESTURE_START)
                        val newNorm = (1f - (offset.y / heightPx)).coerceIn(0f, 1f)
                        val newValue = valueRange.start + newNorm * (valueRange.endInclusive - valueRange.start)
                        onValueChange(newValue)
                    },
                    onDragEnd = { isDragging = false },
                    onDragCancel = { isDragging = false },
                    onDrag = { change, _ ->
                        change.consume()
                        val newNorm = (1f - (change.position.y / heightPx)).coerceIn(0f, 1f)
                        val newValue = valueRange.start + newNorm * (valueRange.endInclusive - valueRange.start)

                        val stepIndex = (newNorm * 20f).toInt().toFloat()
                        if (stepIndex != lastHapticStep) {
                            lastHapticStep = stepIndex
                            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                        }

                        onValueChange(newValue)
                    }
                )
            }
    ) {
        // ─── 1. VERTICAL FLUID FILL TUBE ────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            val fillHeight = size.height * animatedNormalized
            val waterLevel = size.height - fillHeight

            if (fillHeight > 2f) {
                val fillPath = Path().apply {
                    moveTo(0f, size.height)
                    lineTo(size.width, size.height)
                    lineTo(size.width, waterLevel)
                    // Dynamic curved fluid meniscus surface
                    quadraticBezierTo(
                        size.width / 2f,
                        waterLevel - dragTilt,
                        0f,
                        waterLevel
                    )
                    close()
                }

                // Fluid liquid body
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = if (isDark) 0.88f else 0.92f),
                            primaryColor.copy(alpha = if (isDark) 0.42f else 0.52f)
                        ),
                        startY = waterLevel,
                        endY = size.height
                    )
                )

                // Specular liquid crest line
                val meniscusPath = Path().apply {
                    moveTo(0f, waterLevel)
                    quadraticBezierTo(
                        size.width / 2f,
                        waterLevel - dragTilt,
                        size.width,
                        waterLevel
                    )
                }
                drawPath(
                    path = meniscusPath,
                    color = Color.White.copy(alpha = 0.92f),
                    style = Stroke(width = 2.5f)
                )

                // Rising fluid micro-bubbles inside cylinder
                val bubbleCount = 3
                for (b in 0 until bubbleCount) {
                    val bubbleOffsetFraction = ((b * 0.33f) + (animatedNormalized * 1.5f)) % 1f
                    val bx = size.width * (0.3f + b * 0.2f)
                    val by = size.height - (fillHeight * bubbleOffsetFraction)
                    if (by > waterLevel + 6f && by < size.height - 10f) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.45f),
                            radius = 2.5f + b * 1f,
                            center = Offset(bx, by)
                        )
                    }
                }
            }
        }

        // ─── 2. GLYPHS & VALUE PERCENTAGE OVERLAY ───────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val pct = (animatedNormalized * 100f).toInt()
            Text(
                text = "$pct%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.weight(1f))

            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isDark) Color.White else Color(0xFF0F172A),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // ─── 3. ACTIVE DRAGGING FLOATING PERCENTAGE HUD ─────────────────────
        if (hudAlpha > 0.01f) {
            val pct = (animatedNormalized * 100f).toInt()
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp)
                    .graphicsLayer {
                        alpha = hudAlpha
                        scaleX = 0.85f + 0.15f * hudAlpha
                        scaleY = 0.85f + 0.15f * hudAlpha
                    }
                    .pureLiquidGlass(
                        shape = RoundedCornerShape(12.dp),
                        cornerRadius = 12.dp,
                        refraction = 8f,
                        isDark = isDark
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "$pct%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryColor
                )
            }
        }
    }
}
