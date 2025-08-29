package com.frontend.nutricheck.client.ui.view_model.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.frontend.nutricheck.client.ui.view.app_views.RecipeEditorPage
import com.frontend.nutricheck.client.ui.view.app_views.RecipePage
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.FoodProductOverview
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.RecipeOverview
import com.frontend.nutricheck.client.ui.view_model.FoodProductOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeEditorViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeOverviewEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipePageEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipePageViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.ReportRecipeViewModel
import com.frontend.nutricheck.client.ui.view_model.FoodProductOverviewEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeEditorEvent

sealed class RecipePageScreens(val route: String) {
    object RecipePage : RecipePageScreens("recipe_page")
    object RecipeOverview : RecipePageScreens("recipe_overview/{recipeId}") {
        fun createRoute(recipeId: String) = "recipe_overview/$recipeId"
    }
    object FoodProductOverview : AddScreens("food_product_overview/{foodProductId}?recipeId={recipeId}&editable={editable}") {
        fun fromSearch(foodProductId: String) = "food_product_overview/$foodProductId?editable=true"
        fun fromIngredient(recipeId: String, foodProductId: String) =
            "food_product_overview/$foodProductId?recipeId=$recipeId&editable=false"
    }
    object RecipeEditorPage : RecipePageScreens("recipe_editor") {
        const val ARGUMENT = "recipeId"
        const val PATTERN =  "recipe_editor?$ARGUMENT={$ARGUMENT}"
        const val NEW_RECIPE = "recipe_editor"
        fun editRecipe(recipeId: String) = "recipe_editor?$ARGUMENT=$recipeId"
    }
}


@Composable
fun RecipePageNavGraph(
    recipePageNavController: NavHostController
) {
    NavHost(
        navController = recipePageNavController,
        startDestination = RECIPE_PAGE_GRAPH_ROUTE
    ) {
        recipePageGraph(recipePageNavController)
        recipeEditorGraph(recipePageNavController)
        foodProductOverviewRoute(recipePageNavController)
    }
}

private fun NavGraphBuilder.recipePageGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = RecipePageScreens.RecipePage.route,
        route = RECIPE_PAGE_GRAPH_ROUTE
    ) {
        recipePageRoute(navController)
        recipeOverviewRoute(navController)
    }
}

private fun NavGraphBuilder.recipePageRoute(
    navController: NavHostController
) {
    composable(RecipePageScreens.RecipePage.route) { backStackEntry ->
        val parentEntry = rememberParentEntry(navController, RECIPE_PAGE_GRAPH_ROUTE, backStackEntry)
        val recipePageViewModel: RecipePageViewModel = hiltViewModel(parentEntry)
        val reportRecipeViewModel: ReportRecipeViewModel = hiltViewModel(parentEntry)

        HandleRecipePageEvents(recipePageViewModel, navController)

        RecipePage(
            recipePageViewModel = recipePageViewModel,
            reportRecipeViewModel = reportRecipeViewModel,
            onAddRecipeClick = { navigateToNewRecipe(navController) },
            onItemClick = { recipe -> navigateToRecipeOverview(navController, recipe.id) }
        )
    }
}

private fun NavGraphBuilder.recipeOverviewRoute(
    navController: NavHostController
) {
    composable(
        route = RecipePageScreens.RecipeOverview.route,
        arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
    ) { backStackEntry ->
        val recipeId = requireNotNull(backStackEntry.arguments?.getString("recipeId"))
        val graphEntry = rememberGraphEntry(
            navController,
            RecipePageScreens.RecipeOverview.createRoute(recipeId),
            backStackEntry
        )

        val recipeOverviewViewModel: RecipeOverviewViewModel = hiltViewModel(graphEntry)
        val reportRecipeViewModel: ReportRecipeViewModel = hiltViewModel(graphEntry)

        HandleRecipeOverviewEvents(recipeOverviewViewModel, navController)

        RecipeOverview(
            recipeOverviewViewModel = recipeOverviewViewModel,
            reportRecipeViewModel = reportRecipeViewModel,
            onItemClick = { ingredient ->
                navigateToFoodProductFromIngredient(navController, recipeId, ingredient.foodProduct.id)
            },
            onPersist = { navController.popBackStack() },
            onBack = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.foodProductOverviewRoute(
    navController: NavHostController
) {
    composable(
        route = AddScreens.FoodOverview.route,
        arguments = foodProductOverviewArguments()
    ) { backStackEntry ->
        val args = FoodProductOverviewArgs(backStackEntry.arguments)
        val graphEntry = rememberFoodProductGraphEntry(navController, args, backStackEntry)

        val foodProductOverviewViewModel: FoodProductOverviewViewModel = hiltViewModel(graphEntry)

        HandleFoodProductOverviewEvents(foodProductOverviewViewModel, navController)

        FoodProductOverview(
            foodProductOverviewViewModel = foodProductOverviewViewModel,
            onBack = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.recipeEditorGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = RecipePageScreens.RecipeEditorPage.route,
        route = RECIPE_EDITOR_GRAPH_ROUTE
    ) {
        recipeEditorRoute(navController)
        foodProductOverviewFromEditorRoute(navController)
    }
}

private fun NavGraphBuilder.recipeEditorRoute(
    navController: NavHostController
) {
    composable(
        route = RecipePageScreens.RecipeEditorPage.PATTERN,
        arguments = listOf(
            navArgument(RecipePageScreens.RecipeEditorPage.ARGUMENT) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val parentEntry = rememberParentEntry(navController, RECIPE_EDITOR_GRAPH_ROUTE, backStackEntry)
        val recipeEditorViewModel: RecipeEditorViewModel = hiltViewModel(parentEntry)

        HandleRecipeEditorEvents(recipeEditorViewModel, navController)

        RecipeEditorPage(
            recipeEditorViewModel = recipeEditorViewModel,
            onItemClick = { foodProduct -> navigateToFoodProductFromSearch(navController, foodProduct.id) },
            onBack = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.foodProductOverviewFromEditorRoute(
    navController: NavHostController
) {
    composable(
        route = RecipePageScreens.FoodProductOverview.route,
        arguments = listOf(navArgument("foodProductId") { type = NavType.StringType })
    ) { backStackEntry ->
        val foodProductId = requireNotNull(backStackEntry.arguments?.getString("foodProductId"))
        val graphEntry = rememberGraphEntry(
            navController,
            RecipePageScreens.FoodProductOverview.fromSearch(foodProductId),
            backStackEntry
        )
        val parentEntry = rememberParentEntry(navController, RECIPE_EDITOR_GRAPH_ROUTE, graphEntry)

        val foodProductOverviewViewModel: FoodProductOverviewViewModel = hiltViewModel(graphEntry)
        val recipeEditorViewModel: RecipeEditorViewModel = hiltViewModel(parentEntry)

        HandleIngredientAddedEvent(recipeEditorViewModel, navController)

        FoodProductOverview(
            foodProductOverviewViewModel = foodProductOverviewViewModel,
            recipeEditorViewModel = recipeEditorViewModel,
            onBack = { navController.popBackStack() }
        )
    }
}

// Event Handlers
@Composable
private fun HandleRecipePageEvents(
    viewModel: RecipePageViewModel,
    navController: NavHostController
) {
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            if (event is RecipePageEvent.NavigateToEditRecipe) {
                navigateToEditRecipe(navController, event.recipeId)
            }
        }
    }
}

@Composable
private fun HandleRecipeOverviewEvents(
    viewModel: RecipeOverviewViewModel,
    navController: NavHostController
) {
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is RecipeOverviewEvent.NavigateToEditRecipe -> {
                    navigateToEditRecipe(navController, event.recipeId)
                }
                is RecipeOverviewEvent.RecipeDeleted -> {
                    navController.popBackStack()
                }
                else -> { /* Handle other events if needed */ }
            }
        }
    }
}

@Composable
private fun HandleFoodProductOverviewEvents(
    viewModel: FoodProductOverviewViewModel,
    navController: NavHostController
) {
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            if (event is FoodProductOverviewEvent.UpdateIngredient) {
                navController.popBackStack()
            }
        }
    }
}

@Composable
private fun HandleRecipeEditorEvents(
    viewModel: RecipeEditorViewModel,
    navController: NavHostController
) {
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            if (event is RecipeEditorEvent.RecipeSaved) {
                navController.popBackStack()
            }
        }
    }
}

@Composable
private fun HandleIngredientAddedEvent(
    viewModel: RecipeEditorViewModel,
    navController: NavHostController
) {
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            if (event is RecipeEditorEvent.IngredientAdded) {
                navController.popBackStack()
            }
        }
    }
}

// Helper Functions
@Composable
private fun rememberParentEntry(
    navController: NavHostController,
    route: String,
    backStackEntry: NavBackStackEntry
): NavBackStackEntry = remember(backStackEntry) {
    navController.getBackStackEntry(route)
}

@Composable
private fun rememberGraphEntry(
    navController: NavHostController,
    route: String,
    backStackEntry: NavBackStackEntry
): NavBackStackEntry = remember(backStackEntry) {
    navController.getBackStackEntry(route)
}

@Composable
private fun rememberFoodProductGraphEntry(
    navController: NavHostController,
    args: FoodProductOverviewArgs,
    backStackEntry: NavBackStackEntry
): NavBackStackEntry = remember(backStackEntry) {
    val route = if (args.recipeId != null) {
        RecipePageScreens.FoodProductOverview.fromIngredient(args.recipeId, args.foodProductId)
    } else {
        RecipePageScreens.FoodProductOverview.fromSearch(args.foodProductId)
    }
    navController.getBackStackEntry(route)
}

// Navigation Functions
private fun navigateToNewRecipe(navController: NavHostController) {
    navController.navigate(RecipePageScreens.RecipeEditorPage.NEW_RECIPE)
}

private fun navigateToEditRecipe(navController: NavHostController, recipeId: String) {
    navController.navigate(RecipePageScreens.RecipeEditorPage.editRecipe(recipeId))
}

private fun navigateToRecipeOverview(navController: NavHostController, recipeId: String) {
    navController.navigate(RecipePageScreens.RecipeOverview.createRoute(recipeId))
}

private fun navigateToFoodProductFromIngredient(
    navController: NavHostController,
    recipeId: String,
    foodProductId: String
) {
    navController.navigate(
        RecipePageScreens.FoodProductOverview.fromIngredient(recipeId, foodProductId)
    )
}

private fun navigateToFoodProductFromSearch(
    navController: NavHostController,
    foodProductId: String
) {
    navController.navigate(RecipePageScreens.FoodProductOverview.fromSearch(foodProductId))
}

// Argument Helpers
private fun foodProductOverviewArguments() = listOf(
    navArgument("foodProductId") { type = NavType.StringType },
    navArgument("recipeId") {
        type = NavType.StringType
        nullable = true
    },
    navArgument("editable") {
        type = NavType.StringType
        defaultValue = "true"
    }
)

private data class FoodProductOverviewArgs(
    val foodProductId: String,
    val recipeId: String?
) {
    constructor(arguments: Bundle?) : this(
        foodProductId = requireNotNull(arguments?.getString("foodProductId")),
        recipeId = arguments?.getString("recipeId")
    )
}

// Constants
private const val RECIPE_PAGE_GRAPH_ROUTE = "recipe_page_graph"
private const val RECIPE_EDITOR_GRAPH_ROUTE = "recipe_editor_page_graph"