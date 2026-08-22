package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Direction
import com.example.ui.theme.BackgroundBlack
import com.example.ui.theme.CardBorder
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextSecondary

@Composable
fun DirectionChipsRow(
    currentDirection: Direction,
    onDirectionClick: (Direction) -> Unit,
    modifier: Modifier = Modifier
) {
    val directions = Direction.values()
    val listState = rememberLazyListState()

    LaunchedEffect(currentDirection) {
        val targetIndex = directions.indexOf(currentDirection)
        if (targetIndex >= 0) {
            listState.animateScrollToItem(targetIndex)
        }
    }

    LazyRow(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .testTag("direction_chips_row"),
        horizontalArrangement = Arrangement.Center,
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(directions) { direction ->
            val isCurrent = direction == currentDirection

            Surface(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onDirectionClick(direction) }
                    .testTag("chip_${direction.code.lowercase()}"),
                shape = RoundedCornerShape(12.dp),
                color = if (isCurrent) EmeraldPrimary else SurfaceDark,
                border = BorderStroke(
                    1.dp,
                    if (isCurrent) EmeraldLight else CardBorder
                )
            ) {
                Text(
                    text = direction.code,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = if (isCurrent) BackgroundBlack else TextSecondary,
                    modifier = Modifier.padding(
                        horizontal = if (isCurrent) 22.dp else 16.dp,
                        vertical = 8.dp
                    )
                )
            }
        }
    }
}
