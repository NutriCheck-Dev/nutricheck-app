package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.overview.RecipeOverviewViewModel

@Composable
fun FoodOverview(
    modifier: Modifier = Modifier,
    foodOverviewViewModel: RecipeOverviewViewModel = hiltViewModel(),
    title: String = "Food Overview",
    onBackClick: () -> Unit = {}
) {

}