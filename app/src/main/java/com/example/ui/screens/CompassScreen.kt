package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Direction
import com.example.data.models.SensorAccuracy
import com.example.ui.components.AccuracyBadge
import com.example.ui.components.CalibrationDialog
import com.example.ui.components.CompassDial
import com.example.ui.components.DirectionChipsRow
import com.example.ui.components.DirectionDetailBottomSheet
import com.example.ui.components.VastuSuggestionCard
import com.example.ui.theme.BackgroundBlack
import com.example.ui.theme.CardBorder
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.viewmodel.CompassUiState
import com.example.viewmodel.CompassViewModel

@Composable
fun CompassScreen(
    viewModel: CompassViewModel,
    uiState: CompassUiState,
    modifier: Modifier = Modifier
) {
    val compassData = uiState.compassData
    val settings = uiState.appSettings

    var showCalibrationDialog by remember { mutableStateOf(false) }
    var selectedDirectionDetail by remember { mutableStateOf<Direction?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundBlack)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
            .testTag("compass_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // 1. Top Header: Accuracy Badge on Left & Magnetic Field on Right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AccuracyBadge(
                accuracy = compassData.accuracy,
                onClick = {
                    viewModel.triggerClickHaptic()
                    showCalibrationDialog = true
                }
            )

            // Right side: Magnetic Field readout
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "MAGNETIC FIELD",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp
                    ),
                    color = TextTertiary
                )
                Text(
                    text = "${"%.1f".format(compassData.magneticFieldUt)} µT",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = EmeraldLight
                )
            }
        }

        // Calibration Alert Banner if low accuracy
        AnimatedVisibility(
            visible = compassData.accuracy == SensorAccuracy.LOW || compassData.accuracy == SensorAccuracy.UNRELIABLE,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showCalibrationDialog = true }
                    .testTag("calibration_alert_banner"),
                shape = RoundedCornerShape(12.dp),
                color = SurfaceDark,
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Calibration Alert",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sensor interference detected. Tap to calibrate.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE2E8F0)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Large Precision Compass Dial with Center Degree Overlay
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            CompassDial(
                azimuthDegrees = compassData.azimuthDegrees,
                targetBearing = settings.lockedTargetBearing,
                pitchDegrees = compassData.pitchDegrees,
                rollDegrees = compassData.rollDegrees,
                showLevelBubble = settings.showLevelBubble,
                modifier = Modifier.fillMaxSize()
            )

            // Center Degree & Direction Text Readout
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.testTag("degree_readout")
            ) {
                Text(
                    text = "${compassData.azimuthDegrees.toInt()}°",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.ExtraLight,
                        fontSize = 58.sp,
                        letterSpacing = (-2).sp
                    ),
                    color = TextPrimary
                )

                Text(
                    text = compassData.direction.fullName.uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp,
                        fontSize = 13.sp
                    ),
                    color = EmeraldLight
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bearing lock & Declination summary row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Target Lock Button
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable {
                        viewModel.triggerClickHaptic()
                        viewModel.toggleTargetBearingLock(compassData.azimuthDegrees)
                    }
                    .testTag("lock_bearing_button"),
                shape = RoundedCornerShape(50),
                color = if (settings.lockedTargetBearing != null) EmeraldPrimary.copy(alpha = 0.15f) else SurfaceDark,
                border = BorderStroke(1.dp, if (settings.lockedTargetBearing != null) EmeraldPrimary else CardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (settings.lockedTargetBearing != null) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = "Lock Bearing",
                        tint = if (settings.lockedTargetBearing != null) EmeraldLight else TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (settings.lockedTargetBearing != null) {
                            "LOCKED: ${settings.lockedTargetBearing?.toInt()}°"
                        } else {
                            "LOCK BEARING"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            letterSpacing = 1.0.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (settings.lockedTargetBearing != null) EmeraldLight else TextSecondary
                    )
                }
            }

            // Magnetic Declination Badge
            Surface(
                shape = RoundedCornerShape(50),
                color = SurfaceDark,
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DEC: ${settings.magneticDeclination.toInt()}°",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            letterSpacing = 1.0.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Direction Chips Row
        DirectionChipsRow(
            currentDirection = compassData.direction,
            onDirectionClick = { dir ->
                viewModel.triggerClickHaptic()
                selectedDirectionDetail = dir
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Vastu Suggestion Card
        if (settings.showVastuCard) {
            VastuSuggestionCard(
                direction = compassData.direction,
                onCardClick = {
                    viewModel.triggerClickHaptic()
                    selectedDirectionDetail = compassData.direction
                },
                onCalibrateClick = {
                    viewModel.triggerClickHaptic()
                    showCalibrationDialog = true
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Calibration Dialog Modal
    if (showCalibrationDialog) {
        CalibrationDialog(
            accuracy = compassData.accuracy,
            onDismiss = { showCalibrationDialog = false }
        )
    }

    // Direction Detail Sheet
    if (selectedDirectionDetail != null) {
        DirectionDetailBottomSheet(
            direction = selectedDirectionDetail!!,
            onDismiss = { selectedDirectionDetail = null },
            onLockBearing = {
                viewModel.toggleTargetBearingLock(selectedDirectionDetail!!.centerDegree)
            }
        )
    }
}
