package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.ui.view.app_views.CameraPreviewScreen
import com.frontend.nutricheck.client.ui.view.app_views.CreateMealPage
import com.frontend.nutricheck.client.ui.view.app_views.RecipeEditorPage
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.FoodProductOverview
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.RecipeOverview
import com.frontend.nutricheck.client.ui.view_model.FoodProductOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeEditorViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.ReportRecipeViewModel
import com.frontend.nutricheck.client.ui.view_model.FoodSearchViewModel
import com.frontend.nutricheck.client.ui.view_model.SearchEvent
import com.frontend.nutricheck.client.ui.view_model.FoodProductOverviewEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeEditorEvent

sealed class AddScreens(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    object AddAiMeal : AddScreens("add_ai_meal")

    object AddMeal : AddScreens("add_meal")

    object AddRecipe : AddScreens(
        route = "add_recipe",
        arguments = listOf(
            navArgument("recipeId") {
                type = NavType.StringType
                nullable = true
            }
        )
    )

    object FoodOverview : AddScreens(
        route = "food_product_overview/{foodProductId}?" +
                "recipeId={recipeId}&" +
                "mealId={mealId}&editable={editable}",
        arguments = listOf(
            navArgument("foodProductId") { type = NavType.StringType },
            navArgument("recipeId") {
                type = NavType.StringType
                nullable = true
            },
            navArgument("mealId") {
                type = NavType.StringType
                nullable = true
            },
            navArgument("editable") {
                type = NavType.StringType
                nullable = true
                defaultValue = "true"
            }
        )
    ) {
        fun fromSearch(foodProductId: String) =
            "food_product_overview/$foodProductId?editable=true"

        fun fromIngredient(recipeId: String, foodProductId: String) =
            "food_product_overview/$foodProductId?recipeId=$recipeId&editable=false"

        fun fromAiMeal(mealId: String, foodProductId: String) =
            "food_product_overview/$foodProductId?mealId=$mealId&editable=true"
    }

    object RecipeOverview : AddScreens(
        route = "recipe_overview/{recipeId}?fromSearch={fromSearch}",
        arguments = listOf(
            navArgument("recipeId") { type = NavType.StringType },
            navArgument("fromSearch") { type = NavType.BoolType }
        )
    ) {
        fun createRoute(recipeId: String, fromSearch: Boolean) =
            "recipe_overview/$recipeId?fromSearch=$fromSearch"
    }
}

private object AddNavGraphRoutes {
    const val ROOT = "add_graph"
    const val ADD_MEAL = "add_meal_graph"
    const val ADD_RECIPE = "add_recipe_graph"
    const val ADD_AI_MEAL = "add_ai_meal_graph"
}
@Composable
fun AddNavGraph(
    mainNavController: NavHostController,
    origin: AddDialogOrigin,
) {
    val addNavController = rememberNavController()

    NavHost(
        navController = addNavController,
        startDestination = origin.toStartDestination(),
        route = AddNavGraphRoutes.ROOT
    ) {
        addMealGraph(addNavController, mainNavController)
        addRecipeGraph(addNavController, mainNavController)
        addAiMealGraph(addNavController, mainNavController)
    }
}

// Helper function to keep the startDestination logic clean
private fun AddDialogOrigin.toStartDestination(): String = when (this) {
    AddDialogOrigin.BOTTOM_NAV_BAR_ADD_MEAL -> AddNavGraphRoutes.ADD_MEAL
    AddDialogOrigin.BOTTOM_NAV_BAR_ADD_RECIPE -> AddNavGraphRoutes.ADD_RECIPE
    AddDialogOrigin.BOTTOM_NAV_BAR_ADD_AI_MEAL -> AddNavGraphRoutes.ADD_AI_MEAL
    AddDialogOrigin.RECIPE_PAGE -> AddScreens.AddRecipe.route
}

private fun NavGraphBuilder.addMealGraph(
    addNavController: NavHostController,
    mainNavController: NavHostController
) {
    navigation(
        startDestination = AddScreens.AddMeal.route,
        route = AddNavGraphRoutes.ADD_MEAL
    ) {
        // Screen for creating a meal
        composable(AddScreens.AddMeal.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                addNavController.getBackStackEntry(AddNavGraphRoutes.ADD_MEAL)
            }
            val searchViewModel: FoodSearchViewModel = hiltViewModel(parentEntry)

            HandleMealSavedEvent(mainNavController, searchViewModel)

            CreateMealPage(
                searchViewModel = searchViewModel,
                onItemClick = { foodComponent -> addNavController.navigateToFoodComponent(foodComponent) },
                onBack = { mainNavController.popBackStack() }
            )
        }

        // Screen for viewing a recipe within the add meal flow
        recipeOverviewScreen(navController = addNavController)

        // Screen for viewing a foodProduct within the add meal flow
        foodProductOverviewScreen(
            navController = addNavController,
            parentGraphRoute = AddNavGraphRoutes.ADD_MEAL
        )
    }
}

private fun NavGraphBuilder.addRecipeGraph(
    addNavController: NavHostController,
    mainNavController: NavHostController
) {
    navigation(
        startDestination = AddScreens.AddRecipe.route,
        route = AddNavGraphRoutes.ADD_RECIPE
    ) {
        composable(
            route = AddScreens.AddRecipe.route,
            arguments = listOf(navArgument("recipeId") {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                addNavController.getBackStackEntry(AddNavGraphRoutes.ADD_RECIPE)
            }
            val recipeEditorViewModel: RecipeEditorViewModel = hiltViewModel(parentEntry)

            HandleRecipeSavedEvent(mainNavController, recipeEditorViewModel)

            RecipeEditorPage(
                recipeEditorViewModel = recipeEditorViewModel,
                onItemClick = { foodComponent -> addNavController.navigateToFoodComponent(foodComponent) },
                onBack = { mainNavController.popBackStack() }
            )
        }

        foodProductOverviewScreen(
            navController = addNavController,
            parentGraphRoute = AddNavGraphRoutes.ADD_RECIPE
        )
    }
}

private fun NavGraphBuilder.addAiMealGraph(
    addNavController: NavHostController,
    mainNavController: NavHostController
) {
    navigation(
        startDestination = AddScreens.AddAiMeal.route,
        route = AddNavGraphRoutes.ADD_AI_MEAL
    ) {
        composable(AddScreens.AddAiMeal.route) {
            CameraPreviewScreen(
                addAiMealViewModel = hiltViewModel(),
                onNavigateToFoodProductOverview = { mealId, foodProductId ->
                    addNavController.navigate(AddScreens.FoodOverview.fromAiMeal(mealId, foodProductId))
                },
                onExit = { mainNavController.popBackStack() }
            )
        }

        foodProductOverviewScreen(
            navController = addNavController,
            parentGraphRoute = AddNavGraphRoutes.ADD_AI_MEAL,
            mainNavController = mainNavController
        )
    }
}

// Composable function for the FoodProductOverview screen
private fun NavGraphBuilder.foodProductOverviewScreen(
    navController: NavHostController,
    parentGraphRoute: String,
    mainNavController: NavHostController? = null // Only needed for AI flow
) {
    composable(
        route = AddScreens.FoodOverview.route,
        arguments = AddScreens.FoodOverview.arguments
    ) { backStackEntry ->
        // This ViewModel is always scoped to its own back stack entry
        val foodProductOverviewViewModel: FoodProductOverviewViewModel = hiltViewModel()

        when (parentGraphRoute) {
            AddNavGraphRoutes.ADD_MEAL -> {
                val searchViewModel: FoodSearchViewModel = hiltViewModel(
                    remember(backStackEntry) { navController.getBackStackEntry(parentGraphRoute) }
                )
                FoodProductOverview(
                    foodProductOverviewViewModel = foodProductOverviewViewModel,
                    foodSearchViewModel = searchViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            AddNavGraphRoutes.ADD_RECIPE -> {
                val recipeEditorViewModel: RecipeEditorViewModel = hiltViewModel(
                    remember(backStackEntry) { navController.getBackStackEntry(parentGraphRoute) }
                )
                FoodProductOverview(
                    foodProductOverviewViewModel = foodProductOverviewViewModel,
                    recipeEditorViewModel = recipeEditorViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            AddNavGraphRoutes.ADD_AI_MEAL -> {
                HandleAiMealSubmittedEvent(mainNavController!!, foodProductOverviewViewModel)

                FoodProductOverview(
                    foodProductOverviewViewModel = foodProductOverviewViewModel,
                    onBack = {
                        foodProductOverviewViewModel.onEvent(FoodProductOverviewEvent.DeleteAiMeal)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

// Reusable composable for the RecipeOverview screen
private fun NavGraphBuilder.recipeOverviewScreen(navController: NavHostController) {
    composable(
        route = AddScreens.RecipeOverview.route,
        arguments = AddScreens.RecipeOverview.arguments
    ) {
        // Scoped ViewModels
        val recipeOverviewViewModel: RecipeOverviewViewModel = hiltViewModel()
        val reportRecipeViewModel: ReportRecipeViewModel = hiltViewModel()
        // Shared ViewModel
        val searchViewModel: FoodSearchViewModel = hiltViewModel(
            remember(it) { navController.getBackStackEntry(AddNavGraphRoutes.ADD_MEAL) }
        )

        RecipeOverview(
            recipeOverviewViewModel = recipeOverviewViewModel,
            reportRecipeViewModel = reportRecipeViewModel,
            searchViewModel = searchViewModel,
            onItemClick = { ingredient ->
                navController.navigate(
                    AddScreens.FoodOverview.fromIngredient(ingredient.recipeId, ingredient.foodProduct.id)
                )
            },
            onPersist = { navController.popBackStack() },
            onBack = { navController.popBackStack() }
        )
    }
}

// Centralized navigation logic
private fun NavController.navigateToFoodComponent(foodComponent: FoodComponent) {
    val route = if (foodComponent is FoodProduct) {
        AddScreens.FoodOverview.fromSearch(foodComponent.id)
    } else {
        AddScreens.RecipeOverview.createRoute(foodComponent.id, true)
    }
    navigate(route)
}

// Centralized event handlers
@Composable
private fun HandleMealSavedEvent(
    mainNavController: NavHostController,
    viewModel: FoodSearchViewModel
) {
    LaunchedEffect(viewModel, mainNavController) {
        viewModel.events.collect { event ->
            if (event is SearchEvent.MealSaved) {
                mainNavController.navigateToDiaryAndPop()
            }
        }
    }
}

@Composable
private fun HandleRecipeSavedEvent(
    mainNavController: NavHostController,
    viewModel: RecipeEditorViewModel
) {
    LaunchedEffect(viewModel, mainNavController) {
        viewModel.events.collect { event ->
            if (event is RecipeEditorEvent.RecipeSaved) {
                mainNavController.navigateToDiaryAndPop(DiaryGraphDestination.RECIPE_RELATED)
            }
        }
    }
}

@Composable
private fun HandleAiMealSubmittedEvent(
    mainNavController: NavHostController,
    viewModel: FoodProductOverviewViewModel
) {
    LaunchedEffect(viewModel, mainNavController) {
        viewModel.events.collect { event ->
            if (event is FoodProductOverviewEvent.SubmitMealItem) {
                mainNavController.navigateToDiaryAndPop()
            }
        }
    }
}

// Centralized navigation action
fun NavHostController.navigateToDiaryAndPop(destination: DiaryGraphDestination? = null) {
    navigate(Screen.DiaryPage.createRoute(destination)) {
        popUpTo(Screen.Add.route) { inclusive = true }
        launchSingleTop = true
    }
}
