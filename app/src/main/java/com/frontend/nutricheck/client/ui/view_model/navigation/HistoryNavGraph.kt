package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.ui.view.app_views.HistoryPage
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.FoodProductOverview
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.RecipeOverview
import com.frontend.nutricheck.client.ui.view_model.food.FoodProductOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.overview.RecipeOverviewViewModel

sealed class HistoryPageScreens(val route: String) {
    object HistoryPage : HistoryPageScreens("history_page")
    object FoodProductOverview : RecipePageScreens("food_product_overview/{foodId}") {
        fun createRoute(foodId: String) = "food_product_overview/$foodId"
    }
    object RecipeOverview : RecipePageScreens("recipe_overview/{recipeId}") {
        fun createRoute(recipeId: String) = "recipe_overview/$recipeId"
    }
    object AddMeal : RecipePageScreens("add_meal/{mealId}") {
        fun createRoute(mealId: String) = "add_meal/$mealId"
    }
}

@Composable
fun HistoryPageNavGraph(
    mainNavGraph: NavHostController
) {
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
        composable(HistoryPageScreens.HistoryPage.route) {
            HistoryPage(
                historyViewModel = hiltViewModel(),
                historyPageNavController = historyPageNavController,
                mainNavController = mainNavGraph,
            )
        }
        composable(
            route = "food_details?mealId={mealId}&foodProductId={foodProductId}",
            arguments = listOf(
                navArgument("mealId") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("foodProductId") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val foodProductOverviewViewModel : FoodProductOverviewViewModel = hiltViewModel()
            FoodProductOverview(
                foodProductOverviewViewModel = foodProductOverviewViewModel,
                onBack = { historyPageNavController.popBackStack() }
            )
        }
        composable(
            route = "recipe_details?recipeId={recipeId}&mealId={mealId}",
            arguments = listOf(
                navArgument("recipeId") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("mealId") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { val recipeOverviewViewModel: RecipeOverviewViewModel = hiltViewModel()
            RecipeOverview(
                recipeOverviewViewModel = recipeOverviewViewModel,
                onBack = { historyPageNavController.popBackStack() }
            )
        }
    }
}