package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.frontend.nutricheck.client.ui.view.widgets.DishItemList
import com.frontend.nutricheck.client.ui.view.widgets.FoodComponentSearchBar
import com.frontend.nutricheck.client.ui.view.widgets.MealSelector

@Composable
fun SearchPage(
    modifier: Modifier = Modifier,
    //actions: NavigationActions,
    //searchViewModel: FoodSearchViewModel = hiltViewModel(),
    meal: String = "Mahlzeit auswählen",
    onOptionSelected: (String) -> Unit,
    onDishClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        MealSelector()

        FoodComponentSearchBar()

        DishItemList()
    }
}

@Preview
@Composable
fun SearchPagePreview() {
    SearchPage(
        onOptionSelected = {},
        onDishClick = {},
        onAddClick = {},
        onBack = {}
    )
}