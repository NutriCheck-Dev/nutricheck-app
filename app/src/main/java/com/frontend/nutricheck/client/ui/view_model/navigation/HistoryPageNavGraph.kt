package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct

sealed class HistoryPageScreens(val route: String) {
    object HistoryPage : HistoryPageScreens("history_page")
    object FoodProductOverview : RecipePageScreens("food_product_overview/{foodId}") {
        fun createRoute(foodId: String) = "food_product_overview/$foodId"
    }
    object RecipeOverview : RecipePageScreens("recipe_overview/{recipeId}") {
        fun createRoute(recipeId: String) = "recipe_overview/$recipeId"
    }
}

@Composable
fun HistoryPageNavGraph() {
    val historyPageNavController = rememberNavController()

    fun navigateToFoodComponent(foodComponent: FoodComponent) {
        if (foodComponent is FoodProduct) {
            historyPageNavController.navigate(
                RecipePageScreens.FoodProductOverview.createRoute(foodComponent.id)
            )
        } else {
            historyPageNavController.navigate(
                RecipePageScreens.RecipeOverview.createRoute(foodComponent.id)
            )
        }
    }

    NavHost(
        navController = historyPageNavController,
        startDestination = HistoryPageScreens.HistoryPage.route
    ) {

    }
}