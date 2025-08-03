package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.ui.theme.AppTheme
import com.frontend.nutricheck.client.ui.theme.extended

private data class ScaleDensity(
    override val density: Float,
    override val fontScale: Float
) : Density

@Composable
fun ScalableContent(
    scale: Float,
    content: @Composable () -> Unit
) {
    val currentDens = LocalDensity.current
    val scaled = ScaleDensity(
        density = currentDens.density * scale,
        fontScale = currentDens.fontScale * scale
    )
    CompositionLocalProvider(LocalDensity provides scaled) {
        content()
    }
}


@Composable
fun NutrientChart(
    modifier: Modifier = Modifier,
    nutrient: String = "",
    subtitle: String = "",
    actualValue: Int = 0,
    totalValue: Int = 0,
) {
    val progress = if (totalValue > 0) {
        (actualValue.toFloat() / totalValue).coerceIn(0f, 1f)
    } else 0f
    val styles = MaterialTheme.typography
    val extendedColors = MaterialTheme.extended

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier
            .width(160.dp)
            .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = nutrient,
                style = styles.titleMedium
            )

            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {

                Canvas(modifier = Modifier.matchParentSize()) {
                    val sweepAngle = progress * 360f
                    val stroke = Stroke(width = 25f, cap = StrokeCap.Round)
                    drawArc(
                        color = extendedColors.chartBlue.onColor,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = stroke
                    )
                    drawArc(
                        color = extendedColors.chartBlue.color,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = stroke
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = actualValue.toString(),
                        style = styles.headlineSmall
                    )
                    Text(
                        text = subtitle,
                        style = styles.bodySmall
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun NutrientChartPreview() {
    AppTheme {
        NutrientChart(
            nutrient = "Protein",
            subtitle = "grams",
            actualValue = 275,
            totalValue = 500,
            modifier = Modifier.size(200.dp)
        )
    }
}