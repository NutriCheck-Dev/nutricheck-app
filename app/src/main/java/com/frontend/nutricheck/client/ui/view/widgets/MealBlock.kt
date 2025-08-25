package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.MealItem
import com.frontend.nutricheck.client.model.data_sources.data.MealRecipeItem

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource
import com.frontend.nutricheck.client.R
import java.math.BigDecimal
import java.math.RoundingMode

/**
 *
 */
@Composable
fun MealHeader(
    title: String,
    modifier: Modifier = Modifier,
    calorieCount: Double
) {
    val colors = MaterialTheme.colorScheme
    val roundedCalories = BigDecimal.valueOf(calorieCount)
        .setScale(2, RoundingMode.HALF_UP)
        .toPlainString()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(colors.surfaceContainer),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = colors.onSurfaceVariant,
            lineHeight = 16.sp,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = roundedCalories,
            color = colors.onSurfaceVariant,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MealFooter(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(colors.surfaceContainer)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.label_history_add),
            color = colors.primary,
            lineHeight = 16.sp,
            fontSize = 12.sp,
            modifier = Modifier.clickable(onClick = onAddClick),
        )
    }
}

@Composable
fun MealBlock(
    modifier: Modifier = Modifier,
    mealName: String,
    totalCalories: Double,
    items: List<MealItem>,
    onAddClick: () -> Unit = {},
    onItemClick: (MealItem) -> Unit,
    onRemoveClick: (MealItem) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceContainer)
    ) {
        MealHeader(mealName, calorieCount = totalCalories, modifier = Modifier.padding(horizontal = 16.dp))
        HorizontalDivider(
            color = colors.onSurfaceVariant,
            thickness = 1.dp
        )
        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                when (item) {
                    is MealFoodItem -> {
                        DishItemMealButton(
                            modifier = Modifier.weight(1f), // gibt Platz frei für die drei Punkte
                            title = item.foodProduct.name,
                            calories = item.servings * item.foodProduct.calories * (item.servingSize.getAmount() / 100),
                            onClick = { onItemClick(item) }
                        )
                    }

                    is MealRecipeItem -> {
                        DishItemMealButton(
                            modifier = Modifier.weight(1f),
                            title = item.recipe.name,
                            calories = item.quantity * item.recipe.calories,
                            onClick = { onItemClick(item) }
                        )
                    }
                }

                MealItemMenu(
                    onRemove = { onRemoveClick(item) }
                )
            }

            HorizontalDivider(
                color = colors.onSurfaceVariant,
                thickness = 1.dp
            )
        }
        MealFooter(onAddClick = onAddClick)
    }
}
