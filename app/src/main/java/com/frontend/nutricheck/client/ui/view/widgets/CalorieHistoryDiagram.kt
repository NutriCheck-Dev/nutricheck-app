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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.ui.theme.LocalExtendedColors
import com.frontend.nutricheck.client.ui.view_model.dashboard.CalorieHistoryState
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

enum class CalorieRange(val id: String, val labelResId: Int) {
    LAST_7_DAYS("7d", R.string.range_7_days),
    LAST_30_DAYS("30d", R.string.range_30_days),
    LAST_60_DAYS("60d", R.string.range_60_days)
}
@Composable
fun CalorieHistoryDiagram(
    modifier: Modifier = Modifier,
    calorieHistoryState: CalorieHistoryState,
    selectedRange: CalorieRange,
    onPeriodSelected: (CalorieRange) -> Unit = {},
) {
    val calorieGoal = calorieHistoryState.calorieGoal
    val calorieData = calorieHistoryState.calorieHistory
    val options = CalorieRange.entries

    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .height(184.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surfaceContainer)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.homepage_calorie_history),
                color = colors.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            ChartRangeSwitcher(
                options = options.map { stringResource(it.labelResId) },
                selectedOption = options.indexOf(selectedRange),
                onSelect = { index -> onPeriodSelected(options[index]) }
            )
        }

        Spacer(modifier = Modifier.height(25.dp))
        CalorieBarChart(calorieData, selectedRange, calorieGoal)
    }
}

@Composable
fun CalorieBarChart(
    data: List<Int>,
    selectedRange: CalorieRange,
    calorieGoal: Int,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val extendedColors = LocalExtendedColors.current
    val progressBarColor = extendedColors.chartBlue.color
    val rawMin = data.minOrNull() ?: 0
    val rawMax = data.maxOrNull() ?: 1
    val range = rawMax - rawMin
    val today = LocalDate.now()
    val locale = Locale.GERMAN

    val labelMap = when (selectedRange) {
        CalorieRange.LAST_7_DAYS -> data.indices.associateWith { i ->
            val day = today.minusDays((data.size - 1 - i).toLong())
            // z.B. "Mo", "Di", ...
            day.dayOfWeek.getDisplayName(TextStyle.SHORT, locale).take(2)
        }

        CalorieRange.LAST_30_DAYS -> mapOf(
            (data.size * 1 / 4) to "1W",
            (data.size * 2 / 4) to "2W",
            (data.size * 3 / 4) to "3W",
            (data.size - 1) to "1M"
        )

        CalorieRange.LAST_60_DAYS -> mapOf(
            (data.size * 1 / 4) to "3W",
            (data.size * 2 / 4) to "6W",
            (data.size * 3 / 4) to "9W",
            (data.size - 1) to "3M"
        )

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
                    color = progressBarColor,
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
                        color = colors.onSurface.toArgb()
                        textSize = 26f
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }
    }
}

