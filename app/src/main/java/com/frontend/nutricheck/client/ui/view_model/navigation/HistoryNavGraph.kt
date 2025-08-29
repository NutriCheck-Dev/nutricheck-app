package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.ui.view.app_views.CreateMealPage
import com.frontend.nutricheck.client.ui.view.app_views.HistoryPage
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.FoodProductOverview
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.RecipeOverview
import com.frontend.nutricheck.client.ui.view_model.FoodSearchViewModel
import com.frontend.nutricheck.client.ui.view_model.HistoryViewModel
import com.frontend.nutricheck.client.ui.view_model.SearchEvent
import com.frontend.nutricheck.client.ui.view_model.FoodProductOverviewEvent
import com.frontend.nutricheck.client.ui.view_model.FoodProductOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.ReportRecipeViewModel

sealed class HistoryPageScreens(val route: String) {
    data object HistoryPage : HistoryPageScreens("history_page")
    data object AddMeal : HistoryPageScreens("add_meal?dayTime={dayTime}&date={date}") {
        fun createRoute(dayTime: DayTime, date: Long): String {
            return "add_meal?dayTime=${dayTime.name}&date=$date"
        }
    }
    data object FoodOverview : HistoryPageScreens(
        "food_product_overview/{foodProductId}?recipeId={recipeId}&editable={editable}"
    ) {
        fun fromSearch(foodProductId: String) = "food_product_overview/$foodProductId?editable=true"
        fun fromIngredient(recipeId: String, foodProductId: String) =
            "food_product_overview/$foodProductId?recipeId=$recipeId&editable=false"
    }

    data object RecipeOverview : HistoryPageScreens("recipe_overview/{recipeId}?fromSearch={fromSearch}") {
        fun createRoute(recipeId: String, fromSearch: Boolean): String {
            return "recipe_overview/$recipeId?fromSearch=$fromSearch"
        }
    }
    data object FoodDetails : HistoryPageScreens("food_details?mealId={mealId}&foodProductId={foodProductId}") {
        fun createRoute(mealId: String, foodProductId: String): String {
            return "food_details?mealId=$mealId&foodProductId=$foodProductId"
        }
    }
    data object RecipeDetails : HistoryPageScreens("recipe_details?recipeId={recipeId}&mealId={mealId}") {
        fun createRoute(recipeId: String, mealId: String): String {
            return "recipe_details?recipeId=$recipeId&mealId=$mealId"
        }
    }
    companion object {
        const val ADD_MEAL_GRAPH_ROUTE = "add_meal_graph"
    }
}

@Composable
fun HistoryPageNavGraph(
    historyPageNavController: NavHostController
) {
    NavHost(
        navController = historyPageNavController,
        startDestination = HistoryPageScreens.HistoryPage.route
    ) {
        // Defines the main history page destination
        historyPageScreen(historyPageNavController)

        // Defines the nested graph for adding a meal
        addMealGraph(historyPageNavController)

        // Defines screens for viewing details of an existing meal item
        mealItemDetailsGraph(historyPageNavController)
    }
}

/**
 * Encapsulates the navigation logic for moving from a search result to its overview screen.
 */
private fun navigateToFoodComponent(
    navController: NavHostController,
    foodComponent: FoodComponent
) {
    val route = when (foodComponent) {
        is FoodProduct -> HistoryPageScreens.FoodOverview.fromSearch(foodComponent.id)
        is Recipe -> HistoryPageScreens.RecipeOverview.createRoute(foodComponent.id, fromSearch = true)
    }
    navController.navigate(route)
}

/**
 * Defines the main history page screen.
 */
private fun NavGraphBuilder.historyPageScreen(navController: NavHostController) {
    composable(HistoryPageScreens.HistoryPage.route) {
        val hiltViewModel: HistoryViewModel = hiltViewModel()
        HistoryPage(
            historyViewModel = hiltViewModel,
            historyNavController = navController,
        )
    }
}

/**
 * Defines the nested navigation graph for the "add meal" flow.
 * This graph includes searching for food and viewing product/recipe overviews before adding them.
 */
private fun NavGraphBuilder.addMealGraph(navController: NavHostController) {
    navigation(
        startDestination = HistoryPageScreens.AddMeal.route,
        route = HistoryPageScreens.ADD_MEAL_GRAPH_ROUTE,
    ) {
        addMealScreen(navController)
        foodOverviewScreen(navController)
        recipeOverviewScreen(navController)
    }
}

/**
 * Defines the screen for creating a meal (search, etc.).
 */
private fun NavGraphBuilder.addMealScreen(navController: NavHostController) {
    composable(
        route = HistoryPageScreens.AddMeal.route,
        arguments = listOf(
            navArgument("date") { type = NavType.StringType; nullable = true },
            navArgument("dayTime") { type = NavType.StringType; nullable = true }
        )
    ) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(HistoryPageScreens.ADD_MEAL_GRAPH_ROUTE)
        }
        val searchViewModel: FoodSearchViewModel = hiltViewModel(parentEntry)

        // Pop back when a meal is successfully saved
        LaunchedEffect(searchViewModel) {
            searchViewModel.events.collect { event ->
                if (event is SearchEvent.MealSaved) {
                    navController.popBackStack()
                }
            }
        }

        CreateMealPage(
            searchViewModel = searchViewModel,
            onItemClick = { foodComponent -> navigateToFoodComponent(navController, foodComponent) },
            onBack = { navController.popBackStack() }
        )
    }
}

/**
 * Defines the food product overview screen, used within the "add meal" flow.
 */
private fun NavGraphBuilder.foodOverviewScreen(navController: NavHostController) {
    composable(
        route = HistoryPageScreens.FoodOverview.route,
        arguments = listOf(
            navArgument("foodProductId") { type = NavType.StringType },
            navArgument("recipeId") { type = NavType.StringType; nullable = true },
            navArgument("editable") { type = NavType.StringType; nullable = true; defaultValue = "true" }
        )
    ) { backStack ->
        val searchGraphEntry = remember(backStack) {
            navController.getBackStackEntry(HistoryPageScreens.ADD_MEAL_GRAPH_ROUTE)
        }
        val foodSearchViewModel: FoodSearchViewModel = hiltViewModel(searchGraphEntry)
        val foodProductOverviewViewModel: FoodProductOverviewViewModel = hiltViewModel() // Scoped to this destination

        // Pop back when a food component is added to the meal
        LaunchedEffect(foodSearchViewModel) {
            foodSearchViewModel.events.collect { event ->
                if (event is SearchEvent.AddFoodComponent) {
                    navController.popBackStack()
                }
            }
        }

        FoodProductOverview(
            foodProductOverviewViewModel = foodProductOverviewViewModel,
            foodSearchViewModel = foodSearchViewModel,
            onBack = { navController.popBackStack() }
        )
    }
}

/**
 * Defines the recipe overview screen, used within the "add meal" flow.
 */
private fun NavGraphBuilder.recipeOverviewScreen(navController: NavHostController) {
    composable(
        route = HistoryPageScreens.RecipeOverview.route,
        arguments = listOf(
            navArgument("recipeId") { type = NavType.StringType },
            navArgument("fromSearch") { type = NavType.BoolType }
        )
    ) { backStack ->
        val searchGraphEntry = remember(backStack) {
            navController.getBackStackEntry(HistoryPageScreens.ADD_MEAL_GRAPH_ROUTE)
        }
        val searchViewModel: FoodSearchViewModel = hiltViewModel(searchGraphEntry)
        // These ViewModels are scoped to this specific destination
        val recipeOverviewViewModel: RecipeOverviewViewModel = hiltViewModel()
        val reportRecipeViewModel: ReportRecipeViewModel = hiltViewModel()

        RecipeOverview(
            recipeOverviewViewModel = recipeOverviewViewModel,
            reportRecipeViewModel = reportRecipeViewModel,
            searchViewModel = searchViewModel,
            onItemClick = { ingredient ->
                navController.navigate(
                    HistoryPageScreens.FoodOverview.fromIngredient(
                        ingredient.recipeId,
                        ingredient.foodProduct.id
                    )
                )
            },
            onPersist = { navController.popBackStack() },
            onBack = { navController.popBackStack() }
        )
    }
}


/**
 * A separate graph for viewing details of items *already* in a meal.
 * This avoids mixing concerns with the "add meal" flow.
 */
private fun NavGraphBuilder.mealItemDetailsGraph(navController: NavHostController) {
    foodDetailsScreen(navController)
    recipeDetailsScreen(navController)
}

private fun NavGraphBuilder.foodDetailsScreen(navController: NavHostController) {
    composable(
        route = HistoryPageScreens.FoodDetails.route,
        arguments = listOf(
            navArgument("mealId") { type = NavType.StringType },
            navArgument("foodProductId") { type = NavType.StringType }
        )
    ) {
        val foodProductOverviewViewModel: FoodProductOverviewViewModel = hiltViewModel()

        // Pop back when the meal item is updated/submitted
        LaunchedEffect(foodProductOverviewViewModel) {
            foodProductOverviewViewModel.events.collect { event ->
                if (event is FoodProductOverviewEvent.SubmitMealItem) {
                    navController.popBackStack()
                }
            }
        }

        FoodProductOverview(
            foodProductOverviewViewModel = foodProductOverviewViewModel,
            onBack = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.recipeDetailsScreen(navController: NavHostController) {
    composable(
        route = HistoryPageScreens.RecipeDetails.route,
        arguments = listOf(
            navArgument("recipeId") { type = NavType.StringType },
            navArgument("mealId") { type = NavType.StringType }
        )
    ) {
        val recipeOverviewViewModel: RecipeOverviewViewModel = hiltViewModel()
        val reportRecipeViewModel: ReportRecipeViewModel = hiltViewModel()
        val searchViewModel: FoodSearchViewModel = hiltViewModel()

        RecipeOverview(
            recipeOverviewViewModel = recipeOverviewViewModel,
            reportRecipeViewModel = reportRecipeViewModel,
            searchViewModel = searchViewModel,
            onItemClick = { ingredient ->
                navController.navigate(
                    HistoryPageScreens.FoodOverview.fromIngredient(
                        ingredient.recipeId,
                        ingredient.foodProduct.id
                    )
                )
            },
            onPersist = { navController.popBackStack() },
            onBack = { navController.popBackStack() }
        )
    }
}