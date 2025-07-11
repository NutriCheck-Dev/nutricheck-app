package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.ui.layout.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun CalorieHistoryDiagram(
    modifier: Modifier = Modifier,
    title: String = "Kalorienübersicht",
    calorieData: List<Float> = listOf(1850f, 1964f, 2078f, 2000f, 1980f, 1990f, 2020f),
    selectedRange: String = "7T",
    onPeriodSelected: (String) -> Unit = {},
    firstPeriod: String = "7T",
    secondPeriod: String = "30T",
    thirdPeriod: String = "90T"
) {
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
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            ChartRangeSwitcher(
                selectedPeriod = selectedRange,
                onPeriodSelected = onPeriodSelected,
                firstPeriod = firstPeriod,
                secondPeriod = secondPeriod,
                thirdPeriod = thirdPeriod
            )
        }

        Spacer(modifier = Modifier.height(23.dp))

        CaloryBarChart(calorieData, selectedRange)
    }
}

@Composable
fun CaloryBarChart(data: List<Float>, selectedRange: String, modifier: Modifier = Modifier) {
    val maxValue = data.maxOrNull() ?: 1f
    val minValue = data.minOrNull() ?: 0f

    val steps = 3
    val yStep = (maxValue - minValue) / steps

    val yLabels = (0..steps).map { i -> minValue + i * yStep }

    val labelMap = when (selectedRange) {
        "7T" -> mapOf(0 to "Mo", 1 to "Di", 2 to "Mi", 3 to "Do", 4 to "Fr", 5 to "Sa", 6 to "So")
        "30T" -> mapOf(7 to "1W", 14 to "2W", 29 to "1M")
        "90T" -> mapOf(30 to "1M", 60 to "2M", 89 to "3M")
        else -> emptyMap()
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .height(66.dp)
                .padding(horizontal = 30.dp)
        ) {
            val barWidth = 12.dp.toPx()
            val spacing = size.width / data.size.coerceAtLeast(1)
            val heightRatio = size.height / (maxValue - minValue)

            // Y-Achse
            yLabels.forEach { yValue ->
                val y = size.height - (yValue - minValue) * heightRatio
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 2f
                )
                drawContext.canvas.nativeCanvas.drawText(
                    "%.0f".format(yValue),
                    -4.5f,
                    y + 12f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 30f
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }
                )
            }

            // Balken
            data.forEachIndexed { index, value ->
                val xCenter = spacing * index + spacing / 2
                val barHeight = (value - minValue) * heightRatio
                drawRect(
                    color = Color(0xFF42A5F5),
                    topLeft = Offset(xCenter - barWidth / 2, size.height - barHeight),
                    size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
                )
            }

            // X-Achse Labels
            labelMap.forEach { (index, label) ->
                val x = spacing * index + spacing / 2
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



@Preview(showBackground = true)
@Composable
fun BarChartPreview() {
    CalorieHistoryDiagram()
}
