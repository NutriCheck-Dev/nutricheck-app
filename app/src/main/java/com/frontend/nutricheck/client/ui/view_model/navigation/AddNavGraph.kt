package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.ui.view.app_views.CameraPreviewScreen
import com.frontend.nutricheck.client.ui.view.app_views.CreateMealPage
import com.frontend.nutricheck.client.ui.view.app_views.RecipeEditorPage
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.FoodProductOverview
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.RecipeOverview
import com.frontend.nutricheck.client.ui.view_model.food.FoodProductOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeEditorViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.ReportRecipeViewModel
import com.frontend.nutricheck.client.ui.view_model.FoodSearchViewModel
import com.frontend.nutricheck.client.ui.view_model.SearchEvent
import com.frontend.nutricheck.client.ui.view_model.food.FoodProductOverviewEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeEditorEvent

sealed class AddScreens(val route: String) {
    object AddMealGraph : AddScreens("add_meal_graph")
    object AddRecipeGraph : AddScreens("add_recipe_graph")
    object AddAiMealGraph : AddScreens("add_ai_meal_graph")
    object AddAiMeal : AddScreens("add_ai_meal")
    object AddMeal : AddScreens("add_meal?recipeId={recipeId}") {
        const val defaultRoute = "add_meal"
    }
    object AddRecipe : AddScreens("add_recipe")
    object RecipePage : AddScreens("recipe_page")
    object FoodOverview : AddScreens(
        "food_product_overview/{foodProductId}" +
                "?recipeId={recipeId}&mealId={mealId}") {
        fun fromSearch(foodProductId: String) = "food_product_overview/$foodProductId"
        fun fromIngredient(recipeId: String, foodProductId: String) =
            "food_product_overview/$foodProductId?recipeId=$recipeId"
        fun fromAiMeal(mealId: String, foodProductId: String) =
            "food_product_overview/$foodProductId?mealId=$mealId"
    }
    object RecipeOverview : AddScreens("recipe_overview/{recipeId}?fromSearch={fromSearch}") {
        fun createRoute(recipeId: String, fromSearch: Boolean) =
            "recipe_overview/$recipeId?fromSearch=$fromSearch"

    }

}
@Composable
fun AddNavGraph(mainNavController: NavHostController,
                origin: AddDialogOrigin,
                date: Long?,
                dayTime: DayTime?) {
    val addNavController = rememberNavController()

    fun navigateToFoodComponent(foodComponent: FoodComponent) {
        if (foodComponent is FoodProduct) {
            addNavController.navigate(AddScreens.FoodOverview.fromSearch(foodComponent.id))
        } else { addNavController.navigate(AddScreens.RecipeOverview.createRoute(foodComponent.id, true))}
    }

    NavHost(
        navController = addNavController,
        startDestination = when(origin) {
            AddDialogOrigin.BOTTOM_NAV_BAR_ADD_MEAL -> AddScreens.AddMealGraph.route
            AddDialogOrigin.BOTTOM_NAV_BAR_ADD_RECIPE -> AddScreens.AddRecipeGraph.route
            AddDialogOrigin.BOTTOM_NAV_BAR_ADD_AI_MEAL -> AddScreens.AddAiMealGraph.route
            AddDialogOrigin.RECIPE_PAGE -> AddScreens.AddRecipe.route
            AddDialogOrigin.HISTORY_PAGE -> AddScreens.AddMeal.defaultRoute
        },
        route = "add_graph"
    ) {
        navigation(
            startDestination = AddScreens.AddMeal.defaultRoute,
            route = "add_meal_graph"
        ) {
            composable(AddScreens.AddMeal.defaultRoute) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    addNavController.getBackStackEntry("add_meal_graph")
                }
                LaunchedEffect(Unit) {
                    date?.let {
                        if (parentEntry.savedStateHandle.get<String>("date") == null) {
                            parentEntry.savedStateHandle["date"] = it
                        }
                    }
                    dayTime?.let {
                        if (parentEntry.savedStateHandle.get<String>("dayTime") == null) {
                            parentEntry.savedStateHandle["dayTime"] = it
                        }
                    }
                }
                val searchViewModel: FoodSearchViewModel = hiltViewModel(parentEntry)

                LaunchedEffect(searchViewModel) {
                    searchViewModel.events.collect { event ->
                        if (event is SearchEvent.MealSaved) {
                            mainNavController.navigate(Screen.DiaryPage.route) {
                                popUpTo(Screen.Add.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                }
                CreateMealPage(
                    searchViewModel = searchViewModel,
                    onItemClick = { foodComponent -> navigateToFoodComponent(foodComponent) },
                    onBack = { mainNavController.popBackStack() }
                )
            }

            composable (
                route = AddScreens.RecipeOverview.route,
                arguments = listOf(
                    navArgument("recipeId") { type = NavType.StringType },
                    navArgument("fromSearch") { type = NavType.BoolType }
                )
            ) { backStack ->
                val recipeId = backStack.arguments!!.getString("recipeId")!!
                val fromSearch = backStack.arguments!!.getBoolean("fromSearch")
                val graphEntry = remember(backStack) {
                    addNavController.getBackStackEntry(
                        AddScreens.RecipeOverview.createRoute(recipeId, fromSearch)
                    )
                }
                val searchGraphEntry = remember(backStack) {
                    addNavController.getBackStackEntry("add_meal_graph")
                }
                val recipeOverviewViewModel: RecipeOverviewViewModel = hiltViewModel(graphEntry)
                val reportRecipeViewModel: ReportRecipeViewModel = hiltViewModel(graphEntry)
                val searchViewModel: FoodSearchViewModel = hiltViewModel(searchGraphEntry)
                RecipeOverview(
                    recipeOverviewViewModel = recipeOverviewViewModel,
                    reportRecipeViewModel = reportRecipeViewModel,
                    searchViewModel = searchViewModel,
                    onItemClick = { ingredient ->
                        addNavController
                            .navigate(AddScreens.FoodOverview.fromIngredient(ingredient.recipeId, ingredient.foodProduct.id))
                    },
                    onPersist = { addNavController.popBackStack() },
                    onBack = { addNavController.popBackStack() }
                )
            }

            composable (
                route = AddScreens.FoodOverview.route,
                arguments = listOf(
                    navArgument("foodProductId") { type = NavType.StringType },
                    navArgument("recipeId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("mealId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStack ->
                val foodProductId = backStack.arguments!!.getString("foodProductId")!!
                val recipeId = backStack.arguments!!.getString("recipeId")
                val mealId = backStack.arguments!!.getString("mealId")
                val mode = when {
                    recipeId != null -> AddScreens.FoodOverview.fromIngredient(recipeId, foodProductId)
                    mealId != null -> AddScreens.FoodOverview.fromAiMeal(mealId, foodProductId)
                    else -> AddScreens.FoodOverview.fromSearch(foodProductId)
                }
                val graphEntry = remember(backStack) {
                    addNavController.getBackStackEntry(mode)
                }
                val searchGraphEntry = remember(backStack) {
                    addNavController.getBackStackEntry("add_meal_graph")
                }
                val foodProductOverviewViewModel: FoodProductOverviewViewModel = hiltViewModel(graphEntry)
                val foodSearchViewModel: FoodSearchViewModel = hiltViewModel(searchGraphEntry)

                LaunchedEffect(foodSearchViewModel) {
                    foodSearchViewModel.events.collect { event ->
                        if (event is SearchEvent.AddFoodComponent) {
                            addNavController.popBackStack()
                        }
                    }
                }

                FoodProductOverview(
                    foodProductOverviewViewModel = foodProductOverviewViewModel,
                    foodSearchViewModel = foodSearchViewModel,
                    onBack = { addNavController.popBackStack() }
                )
            }
        }

        navigation(
            startDestination = AddScreens.AddRecipe.route,
            route = "add_recipe_graph"
        ) {
            composable(
                route = AddScreens.AddRecipe.route,
                arguments = listOf(
                    navArgument("recipeId") {
                        type = NavType.StringType
                        nullable = true
                    }
                )
            ) { backStack ->
                val searchGraphEntry = remember(backStack) {
                    addNavController.getBackStackEntry("add_recipe_graph")
                }
                val createRecipeViewModel: RecipeEditorViewModel = hiltViewModel(searchGraphEntry)
                RecipeEditorPage(
                    recipeEditorViewModel = createRecipeViewModel,
                    onItemClick = { foodComponent -> navigateToFoodComponent(foodComponent) },
                    onSave = { addNavController.navigate(AddScreens.RecipePage.route)},
                    onBack = { addNavController.popBackStack() }
                )
            }

            composable(
                route = AddScreens.FoodOverview.route,
                arguments = listOf(
                    navArgument("foodProductId") { type = NavType.StringType },
                )
            ) { backStack ->
                val foodProductId = backStack.arguments!!.getString("foodProductId")!!
                val graphEntry = remember(backStack) {
                    addNavController.getBackStackEntry(
                        AddScreens.FoodOverview.fromSearch(foodProductId)
                    )
                }
                val searchGraphEntry = remember(backStack) {
                    addNavController.getBackStackEntry("add_recipe_graph")
                }
                val foodProductOverviewViewModel: FoodProductOverviewViewModel = hiltViewModel(graphEntry)
                val recipeEditorViewModel: RecipeEditorViewModel = hiltViewModel(searchGraphEntry)

                LaunchedEffect(recipeEditorViewModel) {
                    recipeEditorViewModel.events.collect { event ->
                        if (event is RecipeEditorEvent.IngredientAdded) {
                            addNavController.popBackStack()
                        }
                    }
                }
                FoodProductOverview(
                    foodProductOverviewViewModel = foodProductOverviewViewModel,
                    recipeEditorViewModel = recipeEditorViewModel,
                    onBack = { addNavController.popBackStack() }
                )
            }
        }

        navigation(
            startDestination = AddScreens.AddAiMeal.route,
            route = "add_ai_meal_graph"
        ) {
            composable(AddScreens.AddAiMeal.route) {
                CameraPreviewScreen(
                    addAiMealViewModel = hiltViewModel(),
                    onNavigateToFoodProductOverview = { mealId, foodProductId ->
                        addNavController.navigate(AddScreens.FoodOverview.fromAiMeal(mealId, foodProductId)) },
                    onExit = { mainNavController.popBackStack() })
            }

            composable (
                route = AddScreens.FoodOverview.route,
                arguments = listOf(
                    navArgument("foodProductId") { type = NavType.StringType },
                    navArgument("mealId") {
                        type = NavType.StringType
                        nullable = true
                    }
                )
            ) { backStack ->
                val foodProductId = backStack.arguments!!.getString("foodProductId")!!
                val mealId = backStack.arguments?.getString("mealId")!!
                val graphEntry = remember(backStack) {
                    addNavController.getBackStackEntry(
                        AddScreens.FoodOverview.fromAiMeal(mealId, foodProductId)
                    )
                }
                val foodProductOverviewViewModel: FoodProductOverviewViewModel = hiltViewModel(graphEntry)

                LaunchedEffect(foodProductOverviewViewModel) {
                    foodProductOverviewViewModel.events.collect { event ->
                        if (event is FoodProductOverviewEvent.SubmitMealItem) {
                            mainNavController.navigate(Screen.DiaryPage.route) {
                                popUpTo(Screen.Add.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                }

                FoodProductOverview(
                    foodProductOverviewViewModel = foodProductOverviewViewModel,
                    onBack = {
                        foodProductOverviewViewModel.onEvent(FoodProductOverviewEvent.DeleteAiMeal)
                        addNavController.popBackStack() }
                )
            }
        }

        composable(AddScreens.RecipePage.route) { RecipePageNavGraph(mainNavController)}
    }
}

