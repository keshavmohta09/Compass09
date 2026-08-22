package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.SensorAccuracy
import com.example.ui.theme.AccuracyHighGreen
import com.example.ui.theme.AccuracyLowOrange
import com.example.ui.theme.AccuracyMediumYellow
import com.example.ui.theme.AccuracyUnreliableRed
import com.example.ui.theme.CardBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextSecondary

@Composable
fun AccuracyBadge(
    accuracy: SensorAccuracy,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val indicatorColor = when (accuracy) {
        SensorAccuracy.HIGH -> AccuracyHighGreen
        SensorAccuracy.MEDIUM -> AccuracyMediumYellow
        SensorAccuracy.LOW -> AccuracyLowOrange
        SensorAccuracy.UNRELIABLE, SensorAccuracy.NO_SENSOR -> AccuracyUnreliableRed
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .clickable { onClick() }
            .testTag("accuracy_badge"),
        shape = RoundedCornerShape(50),
        color = SurfaceDark.copy(alpha = 0.6f),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Glowing Indicator Dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(indicatorColor)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "ACCURACY: ${accuracy.label.uppercase()}",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                ),
                color = TextSecondary
            )
        }
    }
}
