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

package com.android.systemui.notifications.ui.composable

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.compose.modifiers.pureLiquidGlass
import kotlin.math.roundToInt

data class XephiraNotifItem(
    val id: String,
    val appName: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val icon: ImageVector,
    val accentColor: Color,
    val actionLabel: String? = null
)

/**
 * Next-Gen Liquid Glass Notification Drawer (Android 17 / iOS 26 Inspired).
 * Features rich frosted glass cards, fluid swipe-to-dismiss spring physics,
 * tactile clear-all actions, and synchronized Control Center segment navigation.
 */
@Composable
fun XephiraNotificationPanel(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    onSwitchToControlCenter: (() -> Unit)? = null,
    onOpenSettings: (() -> Unit)? = null
) {
    val view = LocalView.current

    val notifications = remember {
        mutableStateListOf(
            XephiraNotifItem(
                id = "1",
                appName = "Messages",
                title = "Elena Rostova",
                message = "The new Liquid Glass build is looking incredible! Check the haptics.",
                timeAgo = "2m ago",
                icon = Icons.Filled.Message,
                accentColor = Color(0xFF38BDF8),
                actionLabel = "Reply"
            ),
            XephiraNotifItem(
                id = "2",
                appName = "Xephira System",
                title = "System Performance Optimal",
                message = "120Hz Fluid Motion enabled. Memory pressure low (1.8 GB free).",
                timeAgo = "15m ago",
                icon = Icons.Filled.CheckCircle,
                accentColor = Color(0xFF10B981)
            ),
            XephiraNotifItem(
                id = "3",
                appName = "Mail",
                title = "Google DeepMind Team",
                message = "Android 17 Liquid Glass architectural review approved.",
                timeAgo = "45m ago",
                icon = Icons.Filled.Mail,
                accentColor = Color(0xFF8B5CF6),
                actionLabel = "Archive"
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ─── 1. TOP DUAL-SEGMENT PILL SWITCHER & QUICK ACTIONS ─────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Segment Switcher Pill
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
                // Control Center Switcher Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                            onSwitchToControlCenter?.invoke()
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Control Center",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Color(0x99FFFFFF) else Color(0xFF64748B)
                    )
                }

                // Notifications Active Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isDark) Color(0x35FFFFFF) else Color(0xFF0F172A)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Notifications",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Action Toolbar (Clear All + Settings)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (notifications.isNotEmpty()) {
                    // Clear All Frosted Button
                    Row(
                        modifier = Modifier
                            .pureLiquidGlass(
                                shape = RoundedCornerShape(14.dp),
                                cornerRadius = 14.dp,
                                refraction = 8f,
                                isDark = isDark
                            )
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                                notifications.clear()
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ClearAll,
                            contentDescription = "Clear All",
                            tint = if (isDark) Color(0xCCFFFFFF) else Color(0xFF0F172A),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Clear",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color(0xCCFFFFFF) else Color(0xFF0F172A)
                        )
                    }
                }

                // Settings Button
                Surface(
                    shape = CircleShape,
                    color = if (isDark) Color(0x22FFFFFF) else Color(0x14000000),
                    border = BorderStroke(1.dp, if (isDark) Color(0x35FFFFFF) else Color(0x15000000)),
                    modifier = Modifier
                        .size(34.dp)
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
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }

        // ─── 2. ELEGANT CLOCK & DATE BANNER ─────────────────────────────────
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            Text(
                text = "12:58",
                fontSize = 44.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDark) Color.White else Color(0xFF0F172A),
                letterSpacing = (-1.5).sp
            )
            Text(
                text = "Wednesday, September 16",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDark) Color(0x99FFFFFF) else Color(0xFF64748B)
            )
        }

        // ─── 3. NOTIFICATION STACK OR EMPTY STATE ───────────────────────────
        if (notifications.isEmpty()) {
            // Ethereal Liquid Empty State
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .pureLiquidGlass(
                        shape = RoundedCornerShape(26.dp),
                        cornerRadius = 26.dp,
                        refraction = 14f,
                        isDark = isDark
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LiquidPulsingOrb(isDark = isDark)
                    Text(
                        text = "All Caught Up",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                    Text(
                        text = "No new notifications",
                        fontSize = 12.sp,
                        color = if (isDark) Color(0x80FFFFFF) else Color(0xFF94A3B8)
                    )
                }
            }
        } else {
            // Notification List
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                notifications.forEach { item ->
                    AnimatedVisibility(
                        visible = true,
                        exit = fadeOut(tween(300)) + shrinkVertically(tween(300))
                    ) {
                        SwipeableNotificationCard(
                            item = item,
                            isDark = isDark,
                            onDismiss = {
                                notifications.remove(item)
                                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                            }
                        )
                    }
                }
            }
        }

        // ─── 4. BOTTOM FROSTED DRAG HANDLE ─────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
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
 * Swipeable Notification Card with spring physics and pure liquid glass surface.
 */
@Composable
private fun SwipeableNotificationCard(
    item: XephiraNotifItem,
    isDark: Boolean,
    onDismiss: () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "swipe_card"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
            .graphicsLayer {
                alpha = (1f - (Math.abs(animatedOffsetX) / 600f)).coerceIn(0.2f, 1f)
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (Math.abs(offsetX) > 220f) {
                            onDismiss()
                        } else {
                            offsetX = 0f
                        }
                    },
                    onDragCancel = { offsetX = 0f },
                    onHorizontalDrag = { _, dragAmount ->
                        offsetX += dragAmount
                    }
                )
            }
            .pureLiquidGlass(
                shape = RoundedCornerShape(22.dp),
                cornerRadius = 22.dp,
                refraction = 12f,
                isDark = isDark
            )
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header Row (Icon + App Name + Time)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = item.accentColor.copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, item.accentColor.copy(alpha = 0.6f)),
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = item.accentColor,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    Text(
                        text = item.appName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color(0xB3FFFFFF) else Color(0xFF475569)
                    )
                }

                Text(
                    text = item.timeAgo,
                    fontSize = 10.sp,
                    color = if (isDark) Color(0x66FFFFFF) else Color(0xFF94A3B8)
                )
            }

            // Notification Body (Title + Text)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = item.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF0F172A)
                )
                Text(
                    text = item.message,
                    fontSize = 11.sp,
                    color = if (isDark) Color(0xCCFFFFFF) else Color(0xFF334155),
                    lineHeight = 15.sp
                )
            }

            // Optional Quick Action Pill
            if (item.actionLabel != null) {
                Row(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .pureLiquidGlass(
                            shape = RoundedCornerShape(10.dp),
                            cornerRadius = 10.dp,
                            refraction = 6f,
                            isDark = isDark
                        )
                        .clickable { onDismiss() }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.actionLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = item.accentColor
                    )
                }
            }
        }
    }
}

/**
 * Ethereal iridescent pulsing orb for the empty state.
 */
@Composable
private fun LiquidPulsingOrb(isDark: Boolean) {
    val transition = rememberInfiniteTransition(label = "orb_transition")
    val pulse by transition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_scale"
    )
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orb_rotation"
    )

    Canvas(
        modifier = Modifier
            .size(76.dp)
            .graphicsLayer {
                scaleX = pulse
                scaleY = pulse
                rotationZ = rotation
            }
    ) {
        // Iridescent Glass Aura
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF38BDF8).copy(alpha = 0.55f),
                    Color(0xFF8B5CF6).copy(alpha = 0.28f),
                    Color.Transparent
                )
            ),
            radius = size.width * 0.48f,
            center = Offset(size.width / 2f, size.height / 2f)
        )

        // Specular Rim
        drawCircle(
            color = Color.White.copy(alpha = 0.75f),
            radius = size.width * 0.35f,
            center = Offset(size.width / 2f, size.height / 2f),
            style = Stroke(width = 1.5f)
        )
    }
}
