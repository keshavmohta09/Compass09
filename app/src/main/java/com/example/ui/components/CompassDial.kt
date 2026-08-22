package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BackgroundBlack
import com.example.ui.theme.CardBorder
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SouthSilver
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CompassDial(
    azimuthDegrees: Float,
    targetBearing: Float?,
    pitchDegrees: Float,
    rollDegrees: Float,
    showLevelBubble: Boolean,
    modifier: Modifier = Modifier
) {
    // Animate rotation smoothly
    val animatedAzimuth by animateFloatAsState(
        targetValue = azimuthDegrees,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "compass_rotation"
    )

    Box(
        modifier = modifier.testTag("compass_dial"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val dialRadius = size.minDimension / 2f - 16.dp.toPx()

            // 1. Outer Sophisticated Dark Concentric Rings & Radial Gradient
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF131B2E),
                        Color(0xFF0F172A),
                        Color(0xFF0A0A0A)
                    ),
                    center = center,
                    radius = dialRadius + 14.dp.toPx()
                ),
                radius = dialRadius + 8.dp.toPx(),
                center = center
            )

            // Subtle Outer Slate-800 Ring
            drawCircle(
                color = CardBorder,
                radius = dialRadius + 4.dp.toPx(),
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            // Inner Ring
            drawCircle(
                color = Color(0xFF1E293B).copy(alpha = 0.6f),
                radius = dialRadius - 12.dp.toPx(),
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            // 2. Subtle Crosshair Lines (Sophisticated Low Opacity)
            drawLine(
                color = Color(0xFF334155).copy(alpha = 0.25f),
                start = Offset(center.x, center.y - dialRadius + 8.dp.toPx()),
                end = Offset(center.x, center.y + dialRadius - 8.dp.toPx()),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = Color(0xFF334155).copy(alpha = 0.25f),
                start = Offset(center.x - dialRadius + 8.dp.toPx(), center.y),
                end = Offset(center.x + dialRadius - 8.dp.toPx(), center.y),
                strokeWidth = 1.dp.toPx()
            )

            // 3. Rotating Dial (Ticks, Degree numbers, Cardinal labels)
            rotate(-animatedAzimuth, pivot = center) {
                // North Glowing Needle Beam
                val needleTopY = center.y - dialRadius + 18.dp.toPx()
                val needleBottomY = center.y - 14.dp.toPx()
                val needleBrush = Brush.verticalGradient(
                    colors = listOf(EmeraldLight, EmeraldPrimary, Color.Transparent),
                    startY = needleTopY,
                    endY = needleBottomY + 10.dp.toPx()
                )

                // Emerald Needle Line with Subtle Glow
                drawRoundRect(
                    brush = needleBrush,
                    topLeft = Offset(center.x - 1.5.dp.toPx(), needleTopY),
                    size = Size(3.dp.toPx(), needleBottomY - needleTopY),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )

                drawCompassScale(center = center, radius = dialRadius)

                // Target bearing marker if locked
                if (targetBearing != null) {
                    val targetAngleRad = (targetBearing - 90f) * (PI / 180f).toFloat()
                    val targetMarkerPos = Offset(
                        center.x + (dialRadius - 24.dp.toPx()) * cos(targetAngleRad),
                        center.y + (dialRadius - 24.dp.toPx()) * sin(targetAngleRad)
                    )
                    drawCircle(
                        color = EmeraldPrimary,
                        radius = 5.dp.toPx(),
                        center = targetMarkerPos
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 2.dp.toPx(),
                        center = targetMarkerPos
                    )
                }
            }

            // 4. Static Top Indicator (Precision Reticle Arrow)
            val topTip = Offset(center.x, center.y - dialRadius - 6.dp.toPx())
            val topL = Offset(center.x - 5.dp.toPx(), center.y - dialRadius)
            val topR = Offset(center.x + 5.dp.toPx(), center.y - dialRadius)
            val reticlePath = Path().apply {
                moveTo(topTip.x, topTip.y)
                lineTo(topL.x, topL.y)
                lineTo(topR.x, topR.y)
                close()
            }
            drawPath(path = reticlePath, color = EmeraldLight, style = Fill)

            // 5. Level Bubble Overlay
            if (showLevelBubble) {
                drawLevelBubble(
                    center = center,
                    pitchDegrees = pitchDegrees,
                    rollDegrees = rollDegrees,
                    maxRadius = 32.dp.toPx()
                )
            }

            // 6. Sophisticated Center Hub Pin (Slate-900 with Emerald Border)
            drawCircle(
                color = SurfaceDark,
                radius = 10.dp.toPx(),
                center = center
            )
            drawCircle(
                color = EmeraldPrimary,
                radius = 10.dp.toPx(),
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = EmeraldLight,
                radius = 3.dp.toPx(),
                center = center
            )
        }
    }
}

private fun DrawScope.drawCompassScale(center: Offset, radius: Float) {
    // Draw 360 degree ticks
    for (deg in 0 until 360 step 2) {
        val rad = (deg - 90f) * (PI / 180f).toFloat()
        val isCardinal = deg % 90 == 0
        val isMajor = deg % 30 == 0
        val isIntermediate = deg % 10 == 0

        val tickLength = when {
            isCardinal -> 12.dp.toPx()
            isMajor -> 8.dp.toPx()
            isIntermediate -> 5.dp.toPx()
            else -> 2.5.dp.toPx()
        }

        val strokeWidth = when {
            isCardinal -> 2.dp.toPx()
            isMajor -> 1.5.dp.toPx()
            else -> 1.dp.toPx()
        }

        val tickColor = when {
            deg == 0 -> EmeraldLight
            isCardinal -> Color(0xFFE2E8F0)
            isMajor -> Color(0xFF94A3B8)
            isIntermediate -> Color(0xFF64748B)
            else -> Color(0xFF334155)
        }

        val outerX = center.x + radius * cos(rad)
        val outerY = center.y + radius * sin(rad)
        val innerX = center.x + (radius - tickLength) * cos(rad)
        val innerY = center.y + (radius - tickLength) * sin(rad)

        drawLine(
            color = tickColor,
            start = Offset(innerX, innerY),
            end = Offset(outerX, outerY),
            strokeWidth = strokeWidth
        )
    }

    // Draw Cardinal Labels & Degree Numbers with Android Native Canvas
    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
        }

        val cardinalData = listOf(
            Triple("N", 0f, EmeraldLight),
            Triple("NE", 45f, Color(0xFF94A3B8)),
            Triple("E", 90f, Color(0xFFCBD5E1)),
            Triple("SE", 135f, Color(0xFF94A3B8)),
            Triple("S", 180f, Color(0xFFCBD5E1)),
            Triple("SW", 225f, Color(0xFF94A3B8)),
            Triple("W", 270f, Color(0xFFCBD5E1)),
            Triple("NW", 315f, Color(0xFF94A3B8))
        )

        for ((label, deg, color) in cardinalData) {
            val rad = (deg - 90f) * (PI / 180f).toFloat()
            val textRadius = radius - 26.dp.toPx()
            val x = center.x + textRadius * cos(rad)
            val y = center.y + textRadius * sin(rad) + 5.dp.toPx()

            paint.color = color.hashCode()
            paint.textSize = if (deg % 90 == 0f) 16.dp.toPx() else 11.dp.toPx()
            paint.isFakeBoldText = deg % 90 == 0f
            drawText(label, x, y, paint)
        }

        // Degree numbers
        paint.textSize = 9.dp.toPx()
        paint.isFakeBoldText = false
        paint.color = android.graphics.Color.argb(130, 100, 116, 139)

        for (deg in 0 until 360 step 30) {
            if (deg % 90 == 0) continue
            val rad = (deg - 90f) * (PI / 180f).toFloat()
            val numRadius = radius - 15.dp.toPx()
            val x = center.x + numRadius * cos(rad)
            val y = center.y + numRadius * sin(rad) + 3.dp.toPx()

            drawText(deg.toString(), x, y, paint)
        }
    }
}

private fun DrawScope.drawLevelBubble(
    center: Offset,
    pitchDegrees: Float,
    rollDegrees: Float,
    maxRadius: Float
) {
    // Level boundary ring
    drawCircle(
        color = CardBorder,
        radius = maxRadius,
        center = center,
        style = Stroke(width = 1.dp.toPx())
    )

    // Center target ring
    val isLevel = kotlin.math.abs(pitchDegrees) < 2.5f && kotlin.math.abs(rollDegrees) < 2.5f
    val centerRingColor = if (isLevel) EmeraldLight else Color(0xFF334155)

    drawCircle(
        color = centerRingColor,
        radius = 10.dp.toPx(),
        center = center,
        style = Stroke(width = 1.dp.toPx())
    )

    // Bubble position derived from roll (X) and pitch (Y)
    val clampedRoll = rollDegrees.coerceIn(-30f, 30f)
    val clampedPitch = pitchDegrees.coerceIn(-30f, 30f)

    val offsetX = (clampedRoll / 30f) * (maxRadius - 8.dp.toPx())
    val offsetY = (clampedPitch / 30f) * (maxRadius - 8.dp.toPx())

    val bubbleCenter = Offset(center.x + offsetX, center.y + offsetY)
    val bubbleColor = if (isLevel) EmeraldLight else Color(0xFF64748B)

    drawCircle(
        color = bubbleColor.copy(alpha = 0.2f),
        radius = 7.dp.toPx(),
        center = bubbleCenter
    )
    drawCircle(
        color = bubbleColor,
        radius = 7.dp.toPx(),
        center = bubbleCenter,
        style = Stroke(width = 1.2.dp.toPx())
    )
}
