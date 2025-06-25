package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.frontend.nutricheck.client.ui.view_model.RecipePageViewModel
import com.frontend.nutricheck.client.ui.view_model.navigation.NavigationActions

@Composable
fun RecipePage(
    modifier: Modifier = Modifier,
    actions: NavigationActions,
    viewModel: RecipePageViewModel,
    title: String = "Rezepte",
    onRecipeSelected: (String) -> Unit = {},
    onDetailsCick: (String) -> Unit = {},
    onAddRecipeClick: () -> Unit = {}
) {

}