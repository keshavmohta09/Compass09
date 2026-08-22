package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.models.SensorAccuracy
import com.example.ui.theme.AccuracyHighGreen
import com.example.ui.theme.BackgroundBlack
import com.example.ui.theme.CardBorder
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CalibrationDialog(
    accuracy: SensorAccuracy,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = SurfaceDark,
            border = BorderStroke(1.dp, CardBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("calibration_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SENSOR CALIBRATION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            letterSpacing = 1.4.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = EmeraldLight
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextTertiary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Animated Figure-8 Canvas
                AnimatedFigure8View()

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Wave Phone in a Figure-8",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Rotate and tilt your device smoothly along the infinity loop above to recalibrate internal magnetometer and gyroscope sensors.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Current Live Status Pill
                Surface(
                    shape = RoundedCornerShape(50),
                    color = SurfaceVariantDark,
                    border = BorderStroke(0.5.dp, CardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    when (accuracy) {
                                        SensorAccuracy.HIGH -> AccuracyHighGreen
                                        SensorAccuracy.MEDIUM -> Color(0xFFFBBF24)
                                        SensorAccuracy.LOW -> Color(0xFFFB923C)
                                        SensorAccuracy.UNRELIABLE, SensorAccuracy.NO_SENSOR -> Color(0xFFF87171)
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CURRENT: ${accuracy.label.uppercase()}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.0.sp
                            ),
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Dismiss Button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50))
                        .clickable { onDismiss() }
                        .testTag("calibration_done_button"),
                    shape = RoundedCornerShape(50),
                    color = Color.White
                ) {
                    Text(
                        text = "DONE",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.4.sp
                        ),
                        color = BackgroundBlack,
                        modifier = Modifier.padding(vertical = 12.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedFigure8View() {
    val infiniteTransition = rememberInfiniteTransition(label = "figure8_anim")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "figure8_progress"
    )

    Box(
        modifier = Modifier
            .size(width = 180.dp, height = 90.dp)
            .background(SurfaceVariantDark.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(width = 150.dp, height = 70.dp)) {
            val a = size.width * 0.42f
            val center = Offset(size.width / 2f, size.height / 2f)

            // Draw Lemniscate Path
            val path = Path()
            for (i in 0..120) {
                val t = (i / 120f) * (2 * PI).toFloat()
                val denom = 1 + sin(t) * sin(t)
                val x = center.x + (a * cos(t)) / denom
                val y = center.y + (a * sin(t) * cos(t)) / denom
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()

            drawPath(
                path = path,
                color = Color(0xFF334155),
                style = Stroke(width = 2.dp.toPx())
            )

            // Moving Pulse Dot
            val denom = 1 + sin(progress) * sin(progress)
            val dotX = center.x + (a * cos(progress)) / denom
            val dotY = center.y + (a * sin(progress) * cos(progress)) / denom

            drawCircle(
                color = EmeraldLight.copy(alpha = 0.3f),
                radius = 10.dp.toPx(),
                center = Offset(dotX, dotY)
            )
            drawCircle(
                color = EmeraldPrimary,
                radius = 5.dp.toPx(),
                center = Offset(dotX, dotY)
            )
        }
    }
}
