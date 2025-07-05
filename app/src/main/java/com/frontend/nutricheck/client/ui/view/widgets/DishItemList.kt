package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DishItemList(
    modifier: Modifier = Modifier,
    title: String = "Vorschläge",
    list: List<@Composable () -> Unit> = emptyList()
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(2.dp))

        list.forEach { item ->
            item()
        }
    }
}

@Preview
@Composable
fun DishItemListPreview() {
    DishItemList(
        title = "Vorschläge",
        list = listOf(
            {
                DishItemButton(
                    title = "Gericht 1",
                    subtitle = "200 kcal, 100g",
                    onClick = {},
                    trailingContent = {
                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFFE0E0E0), shape = CircleShape)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Hinzufügen",
                                tint = Color.Black
                            )
                        }
                    })
            },
            {
                DishItemButton(
                    title = "Gericht 2",
                    subtitle = "200 kcal, 100g",
                    onClick = {},
                    trailingContent = {
                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFFE0E0E0), shape = CircleShape)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Hinzufügen",
                                tint = Color.Black
                            )
                        }
                    })
            },
            {
                DishItemButton(
                    title = "Gericht 3",
                    subtitle = "200 kcal, 100g",
                    onClick = {},
                    trailingContent = {
                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFFE0E0E0), shape = CircleShape)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Hinzufügen",
                                tint = Color.Black
                            )
                        }
                    })
            },
            {
                DishItemButton(
                    title = "Gericht 4",
                    subtitle = "200 kcal, 100g",
                    onClick = {},
                    trailingContent = {
                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFFE0E0E0), shape = CircleShape)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Hinzufügen",
                                tint = Color.Black
                            )
                        }
                    })
            },
            {
                DishItemButton(
                    title = "Gericht 5",
                    subtitle = "200 kcal, 100g",
                    onClick = {},
                    trailingContent = {
                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFFE0E0E0), shape = CircleShape)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Hinzufügen",
                                tint = Color.Black
                            )
                        }
                    })
            }
        )
    )
}