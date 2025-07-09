package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.Recipe

@Composable
fun DishItemList(
    modifier: Modifier = Modifier,
    list: List<FoodComponent> = emptyList(),
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        list.forEach { item ->
            DishItemButton(foodComponent = item, trailingContent = { CustomAddButton() })
        }
    }
}

@Preview
@Composable
fun DishItemListPreview() {
    DishItemList(
        list = listOf(
            Recipe(),
            Recipe(),
            Recipe(),
            Recipe()
        )
    )
}