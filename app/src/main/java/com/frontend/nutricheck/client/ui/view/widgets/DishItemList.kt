package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    title: String = "",
    list: List<@Composable () -> Unit> = emptyList(),
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        when(title.isNotBlank()) {
            true -> {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(2.dp))
            }
            false -> {}
        }

        Column(
            Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
            list.forEach { item ->
                item()
            }
        }
    }
}

@Preview
@Composable
fun DishItemListPreview() {
    DishItemList(
        title = "Gerichte",
        list = listOf(
            {
                DishItemButton(
                    title = "Gericht 1",
                    subtitle = "200 kcal, 100g",
                    onClick = {},
                    trailingContent = { CustomAddButton() })
            },
            {
                DishItemButton(
                    title = "Gericht 2",
                    subtitle = "200 kcal, 100g",
                    onClick = {},
                    trailingContent = { CustomAddButton() })
            },
            {
                DishItemButton(
                    title = "Gericht 3",
                    subtitle = "200 kcal, 100g",
                    onClick = {},
                    trailingContent = { CustomAddButton() })
            },
            {
                DishItemButton(
                    title = "Gericht 4",
                    subtitle = "200 kcal, 100g",
                    onClick = {},
                    trailingContent = { CustomAddButton() })
            },
            {
                DishItemButton(
                    title = "Gericht 5",
                    subtitle = "200 kcal, 100g",
                    onClick = {},
                    trailingContent = { CustomAddButton() })
            }
        )
    )
}