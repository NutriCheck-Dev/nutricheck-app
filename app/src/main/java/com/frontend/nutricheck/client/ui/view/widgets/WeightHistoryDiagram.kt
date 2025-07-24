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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frontend.nutricheck.client.ui.view_model.dashboard.weight_history.WeightHistoryState


@Composable
fun WeightHistoryDiagram(
    modifier: Modifier = Modifier,
    weightHistoryState: WeightHistoryState,
    selectedRange: String,
    onPeriodSelected: (String) -> Unit
) {
    //val fullData = weightHistoryState.weightData
    val fullData = listOf(86f, 80f, 85f, 80f, 39f, 82f,90f,86f, 80f, 85f, 80f, 39f, 82f,90f,86f, 80f, 85f, 80f,
        39f, 82f,90f,86f, 80f, 85f, 80f, 39f, 82f,90f,86f, 80f, 85f, 80f, 39f, 82f,90f,86f, 80f, 85f, 80f, 39f, 82f,90f,86f, 80f, 85f, 80f,
        86f, 80f, 85f, 80f, 39f, 82f,90f,86f, 80f, 85f, 80f, 39f, 82f,90f,86f, 80f, 85f, 80f,
        86f, 80f, 85f, 80f, 39f, 82f,90f,86f, 80f, 85f, 80f, 39f, 82f,90f,86f, 80f, 85f, 80f,
        86f, 80f, 85f, 80f, 39f, 82f,90f,86f, 80f, 85f, 80f, 39f, 82f,90f,86f, 80f, 85f, 80f,
        86f, 80f, 85f, 80f, 39f, 82f,90f,86f, 80f, 85f, 80f, 39f, 82f,90f,86f, 80f, 85f, 80f,
        86f, 80f, 85f, 80f, 39f, 82f,90f,86f, 80f, 85f, 80f, 39f, 82f,90f,86f, 80f, 85f, 80f,
        86f, 80f, 85f, 80f, 39f, 82f,90f,86f, 80f, 85f, 80f, 39f, 82f,90f,86f, 80f, 85f, 80f,
        86f, 80f, 85f, 80f, 39f, 82f,90f,86f, 80f, 85f, 80f, 39f, 82f,90f,86f, 80f, 85f, 80f,)

    val displayedData = when (selectedRange) {
        "1M" -> fullData.takeLast(30)
        "6M" -> fullData.takeLast(180)
        "12M" -> fullData
        else -> fullData
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
                text = "Gewichtsfortschritt",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            ChartRangeSwitcher(
                options = listOf("1M", "6M", "12M"),
                selectedOption = listOf("1M", "6M", "12M").indexOf(selectedRange),
                onSelect = { clicked ->
                    onPeriodSelected(clicked)
                }
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
fun WeightLineChart(data: List<Float>, selectedRange: String, modifier: Modifier = Modifier) {
    val maxValue = data.maxOrNull() ?: 1f
    val minValue = data.minOrNull() ?: 0f

    val steps = 3 // Wie viele horizontale Linien (Abstände)
    val yStep = (maxValue - minValue) / steps

    val yLabels = (0..steps).map { i -> minValue + i * yStep }
    val labelMap = when (selectedRange) {
        "1M" -> mapOf(14 to "15T", data.size - 1 to "1M")
        "6M" -> mapOf(
            (data.size * 1 / 3) to "2M",
            (data.size * 2 / 3) to "4M",
            data.size - 1 to "6M"
        )
        "12M" -> mapOf(
            (data.size * 1 / 4) to "3M",
            (data.size * 2 / 4) to "6M",
            (data.size * 3 / 4) to "9M",
            data.size - 1 to "12M"
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
                .height(70.dp)
                .padding(horizontal = 30.dp) // <- exakt das hier

        ) {
            val widthStep = size.width / (data.size - 1).coerceAtLeast(1)
            val heightRatio = size.height / (maxValue - minValue)

            // Hilfslinien & Y-Achse
            yLabels.forEach { yValue ->
                val y = size.height - (yValue - minValue) * heightRatio
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 2f
                )

                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        "%.0f".format(yValue),
                        -4.5f, // leicht nach links verschoben
                        y + 12f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 30f
                            isAntiAlias = true
                            textAlign = android.graphics.Paint.Align.RIGHT
                        }
                    )
                }
            }

            // Linie zeichnen
            val points = data.mapIndexed { index, value ->
                val x = index * widthStep
                val y = size.height - (value - minValue) * heightRatio
                Offset(x, y)
            }

            for (i in 0 until points.size - 1) {
                drawLine(
                    color = Color(0xFF42A5F5),
                    start = points[i],
                    end = points[i + 1],
                    strokeWidth = 4f
                )
            }

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