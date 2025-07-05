package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.ui.view.widgets.FoodComponentSearchBar
import com.frontend.nutricheck.client.ui.view_model.recipe.page.RecipePageViewModel
import com.frontend.nutricheck.client.ui.view_model.navigation.NavigationActions

@Composable
fun RecipePage(
    modifier: Modifier = Modifier,
    actions: NavigationActions,
    viewModel: RecipePageViewModel = hiltViewModel(),
    title: String = "Rezepte",
    localRecipes: List<Recipe> = emptyList(),
    remoteRecipes: List<Recipe> = emptyList(),
    onRecipeSelected: (String) -> Unit = {},
    onDetailsCick: (String) -> Unit = {},
    onAddRecipeClick: () -> Unit = {}
) {

    LazyColumn (
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black)
    ) {
        item { FoodComponentSearchBar() }
    }
}