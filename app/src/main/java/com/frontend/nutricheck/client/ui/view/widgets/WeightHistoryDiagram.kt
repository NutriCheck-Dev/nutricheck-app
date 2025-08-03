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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.ui.view_model.dashboard.WeightHistoryState

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

    val displayedData = when (selectedRange) {
        WeightRange.LAST_1_MONTH -> fullData.takeLast(30)
        WeightRange.LAST_6_MONTHS -> fullData.takeLast(180)
        WeightRange.LAST_12_MONTHS -> fullData
    }
    val currentWeight = fullData.lastOrNull()?.toString() ?: "–"

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
                text = stringResource(id = R.string.homepage_weight_progress),
                color = Color.White,
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
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.End)
        )

        Spacer(modifier = Modifier.height(7.dp))

        WeightLineChart(displayedData, selectedRange)
    }
}

@Composable
fun WeightLineChart(
    data: List<Double>,                // <-- Double passt!
    selectedRange: WeightRange,
    modifier: Modifier = Modifier
) {
    val maxValue = data.maxOrNull() ?: 1.0
    val minValue = data.minOrNull() ?: 0.0

    val steps = 3
    val yStep = (maxValue - minValue) / steps

    val yLabels = (0..steps).map { i -> minValue + i * yStep }

    val labelMap = when (selectedRange) {
        WeightRange.LAST_1_MONTH -> mapOf(
            14 to stringResource(R.string.range_15_days),
            data.size - 1 to stringResource(R.string.range_1_month)
        )
        WeightRange.LAST_6_MONTHS -> mapOf(
            (data.size * 1 / 3) to stringResource(R.string.range_2_months),
            (data.size * 2 / 3) to stringResource(R.string.range_4_months),
            data.size - 1 to stringResource(R.string.range_6_months)
        )
        WeightRange.LAST_12_MONTHS -> mapOf(
            (data.size * 1 / 4) to stringResource(R.string.range_3_months),
            (data.size * 2 / 4) to stringResource(R.string.range_6_months),
            (data.size * 3 / 4) to stringResource(R.string.range_9_months),
            data.size - 1 to stringResource(R.string.range_12_months)
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
            val widthStep = size.width / (data.size - 1).coerceAtLeast(1)
            val heightRatio = size.height / (maxValue - minValue).coerceAtLeast(1.0)

            // Y-Achse Hilfslinien und Labels
            yLabels.forEach { yValue ->
                val y = size.height - (yValue - minValue) * heightRatio
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, y.toFloat()),
                    end = Offset(size.width, y.toFloat()),
                    strokeWidth = 2f
                )
                drawContext.canvas.nativeCanvas.drawText(
                    "%.0f".format(yValue),
                    -4.5f,
                    y.toFloat() + 12f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 30f
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }
                )
            }

            // Datenpunkte verbinden
            val points = data.mapIndexed { index, value ->
                val x = index * widthStep
                val y = size.height - (value - minValue) * heightRatio
                Offset(x.toFloat(), y.toFloat())
            }

            for (i in 0 until points.size - 1) {
                drawLine(
                    color = Color(0xFF42A5F5),
                    start = points[i],
                    end = points[i + 1],
                    strokeWidth = 4f
                )
            }

            // X-Achsen-Labels
            labelMap.forEach { (index, label) ->
                val x = points.getOrNull(index)?.x ?: return@forEach
                drawContext.canvas.nativeCanvas.drawText(
                    label,
                    x,
                    size.height + 50f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 26f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                    }
                )
            }
        }
    }
}