package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.ui.view_model.dashboard.CalorieHistoryState


@Composable
fun CalorieHistoryDiagram(
    modifier: Modifier = Modifier,
    calorieHistoryState: CalorieHistoryState,
    selectedRange : String,
    onPeriodSelected: (String) -> Unit = {},
) {
    val calorieGoal = calorieHistoryState.calorieGoal
    val calorieData = calorieHistoryState.calorieHistory
    Column(
        modifier = modifier
            .height(184.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF121212))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.homepage_calorie_history),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            ChartRangeSwitcher(
                options = listOf("7T", "30T", "60T"),
                selectedOption = listOf("7T", "30T", "60T").indexOf(selectedRange),
                onSelect = { clicked ->
                    onPeriodSelected(clicked)
                }
            )
        }

        Spacer(modifier = Modifier.height(25.dp))
        CalorieBarChart(calorieData, selectedRange, calorieGoal)
    }
}

@Composable
fun CalorieBarChart(
    data: List<Int>,
    selectedRange: String,
    calorieGoal: Int,
    modifier: Modifier = Modifier
) {
    val rawMin = data.minOrNull() ?: 0
    val rawMax = data.maxOrNull() ?: 1
    val range = rawMax - rawMin

    val days = listOf("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So")
    val startIndex = 0 // z.B. wenn Daten bei Montag starten
    val labelMap = when (selectedRange) {
        "7T" -> (0 until data.size).associateWith { i -> days[(startIndex + i) % 7] }
        "30T" -> mapOf(
            (data.size * 1 / 4) to "1W",
            (data.size * 2 / 4) to "2W",
            (data.size * 3 / 4) to "3W",
            (data.size - 1) to "1M"
        )
        "60T" -> mapOf(
            (data.size * 1 / 4) to "3W",
            (data.size * 2 / 4) to "6W",
            (data.size * 3 / 4) to "9W",
            (data.size - 1) to "3M"
        )
        else -> emptyMap()
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .height(82.dp)
                .padding(horizontal = 30.dp)
        ) {
            val barWidth = 12.dp.toPx()

            // Dynamische untere Y-Achsenbegrenzung, alles mit Int!
            val visualMin = if (range < 400) {
                (rawMin - 100).coerceAtLeast(0)
            } else {
                (rawMin * 90 / 100).coerceAtLeast(0) // 0.9 als Int-Operation
            }

            val visualMax = maxOf(rawMax, calorieGoal)
            val heightRatio = if (visualMax - visualMin == 0) 1f else size.height / (visualMax - visualMin).toFloat()

            // Balken
            val totalSpacing = size.width - (data.size * barWidth)
            val spacingBar = if (data.size > 1) totalSpacing / (data.size - 1) else 0f

            data.forEachIndexed { index, value ->
                val x = index * (barWidth + spacingBar)
                val barHeight = ((value - visualMin) * heightRatio).coerceAtLeast(1f)
                drawRect(
                    color = Color(0xFF42A5F5),
                    topLeft = Offset(x, size.height - barHeight),
                    size = Size(barWidth, barHeight)
                )
            }
            // Ziel-Linie
            val goalY = size.height - ((calorieGoal - visualMin).toFloat() / (visualMax - visualMin).toFloat()) * size.height
            drawLine(
                color = Color.Green,
                start = Offset(0f, goalY),
                end = Offset(size.width, goalY),
                strokeWidth = 3f
            )
            // Ziel-Label
            drawContext.canvas.nativeCanvas.drawText(
                "$calorieGoal",
                -4.5f,
                goalY + 12f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.GREEN
                    textSize = 30f
                    isAntiAlias = true
                    textAlign = android.graphics.Paint.Align.RIGHT
                }
            )

            // X-Achse Labels
            labelMap.forEach { (index, label) ->
                val x = index * (barWidth + spacingBar) + barWidth / 2
                drawContext.canvas.nativeCanvas.drawText(
                    label,
                    x,
                    size.height + 40f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 26f
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }
    }
}

