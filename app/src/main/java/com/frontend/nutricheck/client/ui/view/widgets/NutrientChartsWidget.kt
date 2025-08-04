package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Recipe

data class NutrientEntry(
    val label: String,
    val unit: String,
    val actualValue: Double,
    val dailyValue: Double
)

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun RecipeNutrientChartsWidget(
    modifier: Modifier = Modifier,
    recipe: Recipe,
    totalCalories: Double,
    totalCarbs: Double,
    totalProtein: Double,
    totalFat: Double
) {
    val nutrients = listOf(
        NutrientEntry(stringResource(R.string.label_calories), "kcal", recipe.calories, totalCalories),
        NutrientEntry(stringResource(R.string.homepage_nutrition_protein), "g", recipe.protein, totalProtein),
        NutrientEntry(stringResource(R.string.homepage_nutrition_carbs), "g", recipe.carbohydrates, totalCarbs),
        NutrientEntry(stringResource(R.string.homepage_nutrition_fats), "g", recipe.fat, totalFat)
    )
    
    val pages: List<List<NutrientEntry>> = nutrients.chunked(2)
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            pageIndex ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pages[pageIndex].forEach { entry ->
                    AutoSizedNutrientChart(
                        nutrient = entry.label,
                        subtitle = entry.unit,
                        actualValue = entry.actualValue.toInt(),
                        totalValue = entry.dailyValue.toInt(),
                        baseHeight = 180.dp,
                        baseWidth = 160.dp,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (pages[pageIndex].size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val selected = colors.primary
            val unselected = colors.onSurface.copy(alpha = 0.3f) //colors.onSurfaceVariant
            repeat(pagerState.pageCount) { dotIndex ->
                val color = if (pagerState.currentPage == dotIndex) selected else unselected
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun FoodProductNutrientChartsWidget(
    modifier: Modifier = Modifier,
    foodProduct: FoodProduct,
    totalCalories: Double = 0.0,
    totalCarbs: Double = 0.0,
    totalProtein: Double = 0.0,
    totalFat: Double = 0.0
) {
    val nutrients = listOf(
        NutrientEntry(stringResource(R.string.label_calories), "kcal", foodProduct.calories, totalCalories),
        NutrientEntry(stringResource(R.string.homepage_nutrition_protein), "g", foodProduct.protein, totalProtein),
        NutrientEntry(stringResource(R.string.homepage_nutrition_carbs), "g", foodProduct.carbohydrates, totalCarbs),
        NutrientEntry(stringResource(R.string.homepage_nutrition_fats), "g", foodProduct.fat, totalFat)
    )

    Column(
        modifier = Modifier.wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        nutrients.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { entry ->
                    AutoSizedNutrientChart(
                        nutrient = entry.label,
                        subtitle = entry.unit,
                        actualValue = entry.actualValue.toInt(),
                        totalValue = entry.dailyValue.toInt(),
                        baseHeight = 180.dp,
                        baseWidth = 160.dp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

