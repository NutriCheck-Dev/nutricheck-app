package com.frontend.nutricheck.client.ui.view.widgets

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A composable that automatically sizes a NutrientChart based on the available constraints.
 * It scales the chart to fit within the specified base dimensions while maintaining its aspect ratio.
 *
 * @param modifier Modifier to be applied to the chart.
 * @param nutrient The nutrient to be displayed in the chart.
 * @param subtitle The subtitle for the chart.
 * @param actualValue The actual value of the nutrient.
 * @param totalValue The total value of the nutrient.
 * @param baseHeight The base height of the chart, used for scaling.
 * @param baseWidth The base width of the chart, used for scaling.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AutoSizedNutrientChart(
    modifier: Modifier = Modifier,
    nutrient: String,
    subtitle: String,
    actualValue: Int = 700,
    totalValue: Int = 2000,
    baseHeight: Dp = 200.dp,
    baseWidth: Dp = 160.dp
) {
    val baseHeight = baseHeight
    val baseWidth = baseWidth

    BoxWithConstraints(
        modifier = modifier
            .sizeIn(maxWidth = baseWidth, maxHeight = baseHeight),
        contentAlignment = Alignment.Center
    ) {
        val scale = (maxHeight/baseHeight).coerceIn(0f, 1f)

        ScalableContent(scale = scale) {
            NutrientChart(
                modifier = Modifier
                    .size(baseWidth, baseHeight)
                    .scale(scale),
                nutrient = nutrient,
                subtitle = subtitle,
                actualValue = actualValue,
                totalValue = totalValue
            )
        }
    }
}