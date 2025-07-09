package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.ui.theme.AppTheme

data class NutrientEntry(
    val label: String,
    val unit: String,
    val actualValue: Double,
    val dailyValue: Double
)

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun NutrientChartsWidget(
    modifier: Modifier = Modifier,
    recipe: Recipe = Recipe(),
    totalCalories: Double = 0.0,
    totalCarbs: Double = 0.0,
    totalProtein: Double = 0.0,
    totalFat: Double = 0.0
) {
    val nutrients = listOf(
        NutrientEntry("Calories", "kcal", recipe.calories, totalCalories),
        NutrientEntry("Carbs", "g", recipe.carbohydrates, totalCarbs),
        NutrientEntry("Protein", "g", recipe.protein, totalProtein),
        NutrientEntry("Fat", "g", recipe.fat, totalFat)
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        nutrients.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { entry ->
                    NutrientChart(
                        nutrient = entry.label,
                        subtitle = entry.unit,
                        actualValue = entry.actualValue.toInt(),
                        totalValue = entry.dailyValue.toInt()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun NutrientChartsWidgetPreview() {
    AppTheme(darkTheme = true) {
        NutrientChartsWidget(
            recipe = Recipe(
                calories = 500.0,
                carbohydrates = 60.0,
                protein = 30.0,
                fat = 20.0
            ),
            totalCalories = 2000.0,
            totalCarbs = 300.0,
            totalProtein = 150.0,
            totalFat = 70.0
        )
    }
}
