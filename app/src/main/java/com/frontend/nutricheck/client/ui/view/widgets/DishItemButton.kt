package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe

/**
 * A composable function that displays a button for a food component
 * It includes the name, calories, and servings of the food component, and allows for trailing content.
 *
 * @param modifier Modifier to be applied to the button.
 * @param trailingContent Optional trailing content to display alongside the food component.
 * @param foodComponent The food component to display in the button.
 * @param onClick Callback function to handle button clicks.
 */
@Composable
fun DishItemButton(
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)?,
    foodComponent: FoodComponent,
    onClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    val roundedCalories = when (foodComponent) {
        is Recipe -> {
            val calories = foodComponent.calories * foodComponent.servings
            calories.toInt().toString()
        }
        is FoodProduct -> {
            val calories = foodComponent.servings * foodComponent.calories * (foodComponent.servingSize.getAmount() / 100)
            calories.toInt().toString()
        }
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        onClick = { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 64.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = foodComponent.name,
                    style = styles.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(4.dp)
                        .padding(vertical = 4.dp),
                    color = colors.outline
                )

                Text(
                    text = roundedCalories + stringResource(R.string.kilocalories),
                    style = styles.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(0.45f)
                        .padding(start = 8.dp, end = 8.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                trailingContent?.invoke()
            }
        }
    }
}

/**
 * A composable function that displays a button for an ingredient
 * It includes the name, calories, and servings of the ingredient, and allows for trailing content.
 *
 * @param modifier Modifier to be applied to the button.
 * @param trailingContent Optional trailing content to display alongside the ingredient.
 * @param ingredient The ingredient to display in the button.
 * @param onClick Callback function to handle button clicks.
 */
@Composable
fun DishItemButton(
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)?,
    ingredient: Ingredient,
    onClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    val calories = ingredient.servings * ingredient.foodProduct.calories * (ingredient.servingSize.getAmount() / 100)
    val roundedCalories = calories.toInt().toString()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        onClick = { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 64.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = ingredient.foodProduct.name,
                    style = styles.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(4.dp)
                        .padding(vertical = 4.dp),
                    color = colors.outline
                )

                Text(
                    text = roundedCalories + stringResource(R.string.kilocalories),
                    style = styles.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(0.45f)
                        .padding(start = 8.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                trailingContent?.invoke()
            }
        }
    }
}

/**
 * A composable function that displays a button for a meal dish item
 * It includes the title, calories, and quantity of the dish, and allows for click actions.
 *
 * @param modifier Modifier to be applied to the button.
 * @param title The title of the dish.
 * @param calories The calorie count of the dish.
 * @param onClick Callback function to handle button clicks.
 */
@Composable
fun DishItemMealButton(
    modifier: Modifier = Modifier,
    title: String,
    calories: Double,
    onClick: () -> Unit = {},
) {
    val colors = MaterialTheme.colorScheme
    val roundedCalories = calories.toInt().toString()

    Surface(
        modifier = modifier
            .height(40.dp)
            .fillMaxWidth(),
        color = colors.surfaceContainer,
        tonalElevation = 0.dp,
        onClick = { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider(
                    modifier = Modifier
                        .height(30.dp)
                        .width(1.dp),
                    color = colors.outline
                )

                Text(
                    text = roundedCalories + stringResource(R.string.kilocalories),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.weight(0.33f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

