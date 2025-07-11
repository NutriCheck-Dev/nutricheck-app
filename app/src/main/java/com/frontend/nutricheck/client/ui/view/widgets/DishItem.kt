package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

//This file represents a DishItemButton composable function that displays a button for a dish item.
@Composable
fun DishItemButton(
    modifier: Modifier = Modifier,
    title: String = "Gericht",
    subtitle: String = "0 kcal, Portionsgröße",
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .height(52.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF1F1F1F),
        tonalElevation = 0.dp,
        onClick = onClick
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)  // Gap 5
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
                        .height(24.dp)
                        .width(1.dp),
                    color = Color(0xFF6E6E6E)
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFBDBDBD),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            trailingContent?.invoke()
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
        onClick = onClick
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

@Preview
@Composable
fun DishItemButtonPreview() {
    DishItemButton()
    DishItemMealButton(Modifier, "schwanz", 100.0, 2.0)
}