package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.example.data.models.AppSettings
import com.example.data.models.CompassData
import com.example.data.models.Direction
import com.example.data.models.SensorAccuracy
import com.example.ui.components.AccuracyBadge
import com.example.ui.components.CalibrationDialog
import com.example.ui.components.CompassDial
import com.example.ui.components.DirectionChipsRow
import com.example.ui.components.VastuSuggestionCard
import com.example.ui.theme.BackgroundBlack
import com.example.ui.theme.CompassTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class CompassScreenshotsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun capture_compass_dial() {
        composeTestRule.setContent {
            CompassTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundBlack)
                        .padding(24.dp)
                ) {
                    CompassDial(
                        azimuthDegrees = 45f,
                        targetBearing = 45f,
                        pitchDegrees = -1.0f,
                        rollDegrees = 0.5f,
                        showLevelBubble = true,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/compass_dial.png")
    }

    @Test
    fun capture_vastu_card() {
        composeTestRule.setContent {
            CompassTheme {
                Box(
                    modifier = Modifier
                        .background(BackgroundBlack)
                        .padding(16.dp)
                ) {
                    VastuSuggestionCard(
                        direction = Direction.NORTH_EAST,
                        onCardClick = {},
                        onCalibrateClick = {}
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/vastu_card.png")
    }

    @Test
    fun capture_calibration_dialog() {
        composeTestRule.setContent {
            CompassTheme {
                CalibrationDialog(
                    accuracy = SensorAccuracy.MEDIUM,
                    onDismiss = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/calibration_dialog.png")
    }

    @Test
    fun capture_direction_chips() {
        composeTestRule.setContent {
            CompassTheme {
                Box(
                    modifier = Modifier
                        .background(BackgroundBlack)
                        .padding(16.dp)
                ) {
                    DirectionChipsRow(
                        currentDirection = Direction.NORTH_EAST,
                        onDirectionClick = {}
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/direction_chips.png")
    }
}
