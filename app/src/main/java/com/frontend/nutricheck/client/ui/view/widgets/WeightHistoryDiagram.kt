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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Weight
import com.frontend.nutricheck.client.ui.theme.LocalExtendedColors
import com.frontend.nutricheck.client.ui.view_model.dashboard.WeightHistoryState
import java.util.Date
import kotlin.math.floor

enum class WeightRange(val id: String, val labelResId: Int) {
    LAST_1_MONTH("1M", R.string.range_1_month),
    LAST_6_MONTHS("6M", R.string.range_6_months),
    LAST_12_MONTHS("12M", R.string.range_12_months)
}
@Composable
fun WeightHistoryDiagram(
    modifier: Modifier = Modifier,
    weightHistoryState: WeightHistoryState,
    selectedRange: WeightRange,
    onPeriodSelected: (WeightRange) -> Unit
) {
    val fullData = weightHistoryState.weightData
    val currentWeight = fullData.lastOrNull()?.value.toString() ?: "–"
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
                text = stringResource(id = R.string.homepage_weight_progress),
                color = colors.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            val options = WeightRange.entries
            ChartRangeSwitcher(
                options = options.map { stringResource(it.labelResId) },
                selectedOption = options.indexOf(selectedRange),
                onSelect = { index -> onPeriodSelected(options[index]) }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "$currentWeight kg",
            color = colors.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.End)
        )

        Spacer(modifier = Modifier.height(7.dp))

        WeightLineChart(
            data = fullData,
            selectedRange = selectedRange
        )
    }
}
@Composable
fun WeightLineChart(
    data: List<Weight>,
    selectedRange: WeightRange,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val extendedColors = LocalExtendedColors.current
    val lineColor = extendedColors.chartBlue.color
    val textColor = colors.onSurface.toArgb()
    val gridColor = colors.outlineVariant

    if (data.isEmpty()) return

    val now = Date()
    val startDate = when (selectedRange) {
        WeightRange.LAST_1_MONTH -> Date(now.time - 30L * 24 * 60 * 60 * 1000)
        WeightRange.LAST_6_MONTHS -> Date(now.time - 180L * 24 * 60 * 60 * 1000)
        WeightRange.LAST_12_MONTHS -> Date(now.time - 365L * 24 * 60 * 60 * 1000)
    }

    val daysInRange = ((now.time - startDate.time) / (24 * 60 * 60 * 1000)).toInt() + 1

    val weightByDay = data.associateBy {
        it.date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
    }

    val dates = (0 until daysInRange).map { offset ->
        startDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
            .plusDays(offset.toLong())
    }

    // --- Chart-Bereich berechnen ---
    val rawMin = data.minOf { it.value }
    val rawMax = data.maxOf { it.value }

    val minRange = 4.0
    val range = (rawMax - rawMin).coerceAtLeast(minRange)

    val chartMin = floor(rawMin)
    val chartMax = chartMin + range

    // Immer 4 Intervalle -> 5 Labels
    val steps = 4
    val yStep = (chartMax - chartMin) / steps
    val yLabels = (0..steps).map { i -> chartMin + i * yStep }

    val labelPositions = when (selectedRange) {
        WeightRange.LAST_1_MONTH -> listOf(
            1f - (14f / 30f) to stringResource(R.string.range_15_days),
            1f - (29f / 30f) to stringResource(R.string.range_1_month)
        )
        WeightRange.LAST_6_MONTHS -> listOf(
            1f - (60f / 180f) to stringResource(R.string.range_2_months),
            1f - (120f / 180f) to stringResource(R.string.range_4_months),
            1f - (179f / 180f) to stringResource(R.string.range_6_months)
        )
        WeightRange.LAST_12_MONTHS -> listOf(
            1f - (90f / 365f) to stringResource(R.string.range_3_months),
            1f - (180f / 365f) to stringResource(R.string.range_6_months),
            1f - (270f / 365f) to stringResource(R.string.range_9_months),
            1f - (364f / 365f) to stringResource(R.string.range_12_months)
        )
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(horizontal = 30.dp)
        ) {
            val heightRatio = size.height / (chartMax - chartMin).toFloat()
            val dayWidth = size.width / daysInRange.coerceAtLeast(1)

            // --- Y-Achse ---
            yLabels.forEach { yValue ->
                val y = size.height - ((yValue - chartMin) * heightRatio).toFloat()
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 2f
                )
                drawContext.canvas.nativeCanvas.drawText(
                    if (yValue % 1.0 == 0.0) "%.0f".format(yValue) else "%.1f".format(yValue),
                    -10f,
                    y + 12f,
                    android.graphics.Paint().apply {
                        color = textColor
                        textSize = 30f
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }
                )
            }

            // --- Datenpunkte ---
            val points = dates.mapIndexedNotNull { index, localDate ->
                val weight = weightByDay[localDate]
                if (weight != null) {
                    val x = index * dayWidth
                    val y = size.height - ((weight.value - chartMin) * heightRatio).toFloat()
                    x to Offset(x, y)
                } else null
            }

            for (i in 0 until points.size - 1) {
                val (_, p1) = points[i]
                val (_, p2) = points[i + 1]
                drawLine(
                    color = lineColor,
                    start = p1,
                    end = p2,
                    strokeWidth = 4f
                )
            }

            // --- X-Achsen Labels ---
            labelPositions.forEach { (relativePos, label) ->
                val x = size.width * relativePos
                drawContext.canvas.nativeCanvas.drawText(
                    label,
                    x,
                    size.height + 50f,
                    android.graphics.Paint().apply {
                        color = textColor
                        textSize = 26f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                    }
                )
            }
        }
    }
}
