package com.frontend.nutricheck.client.ui.view.widgets

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.ui.theme.AppTheme

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AutoSizedNutrientChart(
    modifier: Modifier = Modifier,
    nutrient: String = "Calories",
    subtitle: String = "kcal",
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

@Preview
@Composable
fun AutoSizedNutrienChartPreview() {
    AppTheme(darkTheme = true) {
        AutoSizedNutrientChart(
            nutrient = "Calories",
            subtitle = "kcal",
            actualValue = 700,
            totalValue = 2000,
            baseHeight = 180.dp
        )
    }
}