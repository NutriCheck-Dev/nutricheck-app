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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
    calorieData: List<Float> = listOf(
        2100f, 1850f, 1930f, 0f, 2400f, 1820f, 2010f, 2200f, 1900f, 2300f,
        0f, 2500f, 1800f, 2100f, 0f, 2300f, 1900f, 1750f, 2000f, 1980f,
        0f, 2400f, 1860f, 2230f, 2100f, 0f, 1950f, 1780f, 2350f, 2010f,
        0f, 1800f, 2200f, 2100f, 2460f, 0f, 1990f, 1820f, 1760f, 2000f,
        0f, 2170f, 1930f, 2300f, 2200f, 0f, 2480f, 1900f, 1750f, 2050f,
        1850f, 0f, 2100f, 1880f, 2350f, 1800f, 0f, 1980f, 1920f, 2220f,
        2400f, 0f, 2100f, 2000f, 1960f, 0f, 2320f, 1830f, 1900f, 1800f,
        0f, 2160f, 2290f, 1990f, 1900f, 2450f, 0f, 2050f, 2200f, 2100f,
        0f, 1800f, 2500f, 1920f, 1980f, 0f, 2380f, 2000f, 1860f, 1900f
    ),
    selectedRange: String = "90T",
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
            var selected by remember { mutableStateOf(1) }
            ChartRangeSwitcher(
                options = listOf("7T", "30T", "90T"),
                selectedOption = selected,
                onSelect = { clicked ->
                    selected = listOf("7T", "30T", "90T").indexOf(clicked)
                }
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

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

    val days = listOf("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So")
    val startIndex = 0 // z. B. wenn Daten bei Montag starten
    val labelMap = when (selectedRange) {
        "7T" -> (0 until data.size).associateWith { i -> days[(startIndex + i) % 7] }
        "30T" -> mapOf(
            (data.size * 1 / 4) to "1W",
            (data.size * 2 / 4) to "2W",
            (data.size * 3 / 4) to "3W",
            (data.size - 1) to "1M"
        )
        "90T" -> mapOf(
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

            val rawMin = data.minOrNull() ?: 0f
            val rawMax = data.maxOrNull() ?: 1f
            val range = rawMax - rawMin

            // Dynamische untere Y-Achsenbegrenzung
            val visualMin = if (range < 400f) {
                (rawMin - 100f).coerceAtLeast(0f)
            } else {
                (rawMin * 0.9f).coerceAtLeast(0f)
            }

            val heightRatio = size.height / (rawMax - visualMin)

            val steps = 3
            val yStep = (rawMax - visualMin) / steps
            val yLabels = (0..steps).map { i -> visualMin + i * yStep }

            // Y-Achse & Hilfslinien
            yLabels.forEach { yValue ->
                val y = size.height - (yValue - visualMin) * heightRatio
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



@Preview(showBackground = true)
@Composable
fun BarChartPreview() {
    CalorieHistoryDiagram()
}
