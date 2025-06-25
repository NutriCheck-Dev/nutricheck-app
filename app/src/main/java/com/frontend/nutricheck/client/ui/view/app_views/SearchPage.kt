package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.ui.view_model.navigation.NavigationActions
import com.frontend.nutricheck.client.ui.view_model.search_food_product.FoodSearchViewModel

@Composable
fun SearchPage(
    modifier: Modifier = Modifier,
    actions: NavigationActions,
    searchViewModel: FoodSearchViewModel = hiltViewModel(),
    meal: String = "Mahlzeit auswählen",
    onOptionSelected: (String) -> Unit,
    onDishClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onBack: () -> Unit = {}
) {}