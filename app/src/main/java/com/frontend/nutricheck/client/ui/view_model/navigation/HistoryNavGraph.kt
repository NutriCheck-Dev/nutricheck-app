package com.frontend.nutricheck.client.ui.view_model.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.ui.view.app_views.CreateMealPage
import com.frontend.nutricheck.client.ui.view.app_views.HistoryPage
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.FoodProductOverview
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.RecipeOverview
import com.frontend.nutricheck.client.ui.view_model.FoodSearchViewModel
import com.frontend.nutricheck.client.ui.view_model.HistoryViewModel
import com.frontend.nutricheck.client.ui.view_model.SearchEvent
import com.frontend.nutricheck.client.ui.view_model.food.FoodProductOverviewEvent
import com.frontend.nutricheck.client.ui.view_model.food.FoodProductOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.ReportRecipeViewModel

sealed class HistoryPageScreens(val route: String) {
    object HistoryPage : HistoryPageScreens("history_page")
    object AddMeal : HistoryPageScreens("add_meal?dayTime={dayTime}&date={date}") {
        fun createRoute(dayTime: DayTime, date: Long): String {
            return "add_meal?dayTime=${dayTime.name}&date=$date"
        }
    }
    object FoodOverview : HistoryPageScreens(
        "food_product_overview/{foodProductId}" +
                "?recipeId={recipeId}") {
        fun fromSearch(foodProductId: String) = "food_product_overview/$foodProductId"
        fun fromIngredient(recipeId: String, foodProductId: String) =
            "food_product_overview/$foodProductId?recipeId=$recipeId"
    }

    object RecipeOverview : HistoryPageScreens("recipe_overview/{recipeId}?fromSearch={fromSearch}") {
        fun createRoute(recipeId: String, fromSearch: Boolean): String {
            return "recipe_overview/$recipeId?fromSearch=$fromSearch"
        }
    }
    object FoodDetails : HistoryPageScreens("food_details?mealId={mealId}&foodProductId={foodProductId}") {
        fun createRoute(mealId: String, foodProductId: String): String {
            return "food_details?mealId=$mealId&foodProductId=$foodProductId"
        }
    }
    object RecipeDetails : HistoryPageScreens("recipe_details?recipeId={recipeId}&mealId={mealId}") {
        fun createRoute(recipeId: String, mealId: String): String {
            return "recipe_details?recipeId=$recipeId&mealId=$mealId"
        }
    }
}

@Composable
fun HistoryPageNavGraph(
    historyPageNavController: NavHostController
) {

    fun navigateToFoodComponent(foodComponent: FoodComponent) {
        if (foodComponent is FoodProduct) {
            historyPageNavController.navigate(HistoryPageScreens.FoodOverview.fromSearch(foodComponent.id))
        } else { historyPageNavController.navigate(HistoryPageScreens.RecipeOverview.createRoute(foodComponent.id, true))}
    }

    NavHost(
        navController = historyPageNavController,
        startDestination = HistoryPageScreens.HistoryPage.route
    ) {
        composable(HistoryPageScreens.HistoryPage.route) {
            val hiltViewModel: HistoryViewModel = hiltViewModel()
            HistoryPage(
                historyViewModel = hiltViewModel,
                historyNavController = historyPageNavController,
            )
        }
        navigation(
            startDestination = HistoryPageScreens.AddMeal.route,
            route = "add_meal_graph",

        ) {
            composable(route = HistoryPageScreens.AddMeal.route,
                arguments = listOf(
                    navArgument("date") {
                        type = NavType.StringType
                        nullable = true
                    },
                    navArgument("dayTime") {
                        type = NavType.StringType
                        nullable = true
                    }
                )
            ) { backStackEntry ->
                val dateArg = backStackEntry.arguments?.getString("date")?.toLongOrNull()
                val dayTimeArg = backStackEntry.arguments?.getString("dayTime")
                    ?.let { runCatching { DayTime.valueOf(it) }.getOrNull() }
                val parentEntry = remember(backStackEntry) {
                    historyPageNavController.getBackStackEntry("add_meal_graph")
                }
                Log.v("RootNavGraph", "→ HistoryNavGraph mit $dateArg / $dayTimeArg")

                val searchViewModel: FoodSearchViewModel = hiltViewModel(parentEntry)
                LaunchedEffect(searchViewModel) {
                    searchViewModel.events.collect { event ->
                        if (event is SearchEvent.MealSaved) {
                            historyPageNavController.popBackStack()
                        }
                    }
                }
                CreateMealPage(
                    searchViewModel = searchViewModel,
                    onItemClick = { foodComponent -> navigateToFoodComponent(foodComponent) },
                    onBack = { historyPageNavController.popBackStack() }
                )
            }

            composable (
                route = HistoryPageScreens.RecipeOverview.route,
                arguments = listOf(
                    navArgument("recipeId") { type = NavType.StringType },
                    navArgument("fromSearch") { type = NavType.BoolType }
                )
            ) { backStack ->
                val recipeId = backStack.arguments!!.getString("recipeId")!!
                val fromSearch = backStack.arguments!!.getBoolean("fromSearch")
                val graphEntry = remember(backStack) {
                    historyPageNavController.getBackStackEntry(
                        HistoryPageScreens.RecipeOverview.createRoute(recipeId, fromSearch)
                    )
                }
                val searchGraphEntry = remember(backStack) {
                    historyPageNavController.getBackStackEntry("add_meal_graph")
                }
                val recipeOverviewViewModel: RecipeOverviewViewModel = hiltViewModel(graphEntry)
                val reportRecipeViewModel: ReportRecipeViewModel = hiltViewModel(graphEntry)
                val searchViewModel: FoodSearchViewModel = hiltViewModel(searchGraphEntry)
                RecipeOverview(
                    recipeOverviewViewModel = recipeOverviewViewModel,
                    reportRecipeViewModel = reportRecipeViewModel,
                    searchViewModel = searchViewModel,
                    onItemClick = { ingredient ->
                        historyPageNavController
                            .navigate(HistoryPageScreens.FoodOverview.fromIngredient(ingredient.recipeId, ingredient.foodProduct.id))
                    },
                    onPersist = { historyPageNavController.popBackStack() },
                    onBack = { historyPageNavController.popBackStack() }
                )
            }

            composable (
                route = HistoryPageScreens.FoodOverview.route,
                arguments = listOf(
                    navArgument("foodProductId") { type = NavType.StringType },
                    navArgument("recipeId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                )
            ) { backStack ->
                val foodProductId = backStack.arguments!!.getString("foodProductId")!!
                val recipeId = backStack.arguments!!.getString("recipeId")
                val mode = when {
                    recipeId != null -> HistoryPageScreens.FoodOverview.fromIngredient(recipeId, foodProductId)
                    else -> HistoryPageScreens.FoodOverview.fromSearch(foodProductId)
                }
                val graphEntry = remember(backStack) {
                    historyPageNavController.getBackStackEntry(mode)
                }
                val searchGraphEntry = remember(backStack) {
                    historyPageNavController.getBackStackEntry("add_meal_graph")
                }
                val foodProductOverviewViewModel: FoodProductOverviewViewModel = hiltViewModel(graphEntry)
                val foodSearchViewModel: FoodSearchViewModel = hiltViewModel(searchGraphEntry)

                LaunchedEffect(foodSearchViewModel) {
                    foodSearchViewModel.events.collect { event ->
                        if (event is SearchEvent.AddFoodComponent) {
                            historyPageNavController.popBackStack()
                        }
                    }
                }

                FoodProductOverview(
                    foodProductOverviewViewModel = foodProductOverviewViewModel,
                    foodSearchViewModel = foodSearchViewModel,
                    onBack = { historyPageNavController.popBackStack() }
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
                val foodProductOverviewViewModel: FoodProductOverviewViewModel = hiltViewModel()
                LaunchedEffect(foodProductOverviewViewModel) {
                    foodProductOverviewViewModel.events.collect { event ->
                        if (event is FoodProductOverviewEvent.SubmitMealItem) {
                            historyPageNavController.popBackStack()
                        }
                    }
                }
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
            ) {
                val recipeOverviewViewModel: RecipeOverviewViewModel = hiltViewModel()
                val reportRecipeViewModel: ReportRecipeViewModel = hiltViewModel()
                val searchViewModel: FoodSearchViewModel = hiltViewModel()
                RecipeOverview(
                    recipeOverviewViewModel = recipeOverviewViewModel,
                    reportRecipeViewModel = reportRecipeViewModel,
                    searchViewModel = searchViewModel,
                    onPersist = { historyPageNavController.popBackStack() },
                    onBack = { historyPageNavController.popBackStack() }
                )
            }
        }

    }
}