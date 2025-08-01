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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient

//This file represents a DishItemButton composable function that displays a button for a dish item.
@Composable
fun DishItemButton(
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = { CustomDetailsButton() },
    foodComponent: FoodComponent,
    onClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
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
                .heightIn(min = 64.dp)
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
                    modifier = Modifier.weight(0.4f)
                )

                VerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(4.dp)
                        .padding(vertical = 4.dp),
                    color = colors.outline
                )

                Text(
                    text = "${foodComponent.calories} cal, Portionsgröße ${foodComponent.fat}",
                    style = styles.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(0.6f)
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

@Composable
fun DishItemButton(
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = { CustomDetailsButton() },
    ingredient: Ingredient,
    onClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
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
                .heightIn(min = 64.dp)
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
                    modifier = Modifier.weight(0.4f)
                )

                VerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .padding(vertical = 4.dp),
                    color = colors.outline
                )

                Text(
                    text = "${ingredient.foodProduct.calories} cal, Portionsgröße ${ingredient.quantity}",
                    style = styles.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(0.6f)
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

@Composable
fun DishItemMealButton(
    modifier: Modifier = Modifier,
    title: String,
    calories: Double,
    quantity: Double,
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier
            .height(40.dp)
            .fillMaxWidth(),
        color = Color(0xFF121212),
        tonalElevation = 0.dp,
        onClick = { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
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
                    color = Color.White
                )

                VerticalDivider(
                    modifier = Modifier
                        .height(30.dp)
                        .width(1.dp),
                    color = Color(0xFF6E6E6E)
                )

                Text(
                    text = "${calories.toInt()} kcal, ${quantity}g",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFBDBDBD),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
