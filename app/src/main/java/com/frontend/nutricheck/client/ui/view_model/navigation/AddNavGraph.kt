package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.frontend.nutricheck.client.ui.view.app_views.CreateRecipePage
import com.frontend.nutricheck.client.ui.view.app_views.FoodOverview
import com.frontend.nutricheck.client.ui.view.app_views.RecipeOverview
import com.frontend.nutricheck.client.ui.view.app_views.SearchPage
import com.frontend.nutricheck.client.ui.view.dialogs.AddDialog
import com.frontend.nutricheck.client.ui.view_model.add_components.AddDialogEvent
import com.frontend.nutricheck.client.ui.view_model.add_components.AddDialogViewModel

sealed class AddScreens(val route: String) {
    object AddMainPage : Screen("add")
    object AddAiMeal : AddScreens("add_ai_meal")
    object AddMeal : AddScreens("add_meal")
    object AddRecipe : AddScreens("add_recipe")
    object MealOverview : AddScreens("meal_overview")
    object FoodOverview : AddScreens("food_overview")
    object RecipeOverview : AddScreens("recipe_overview")

}
@Composable
fun AddNavGraph(mainNavController: NavHostController, origin: AddDialogOrigin) {
    val addNavController = rememberNavController()
    val addDialogViewModel: AddDialogViewModel = hiltViewModel()

    var startDestination = when (origin) {
        AddDialogOrigin.BOTTOM_NAV_BAR -> {
            AddScreens.AddMainPage.route
        }
        AddDialogOrigin.RECIPE_PAGE -> {
            AddScreens.AddRecipe.route
        }
        AddDialogOrigin.HISTORY_PAGE -> {
            AddScreens.AddMeal.route
        }
    }

    LaunchedEffect(Unit) {
        addDialogViewModel.events.collect { event ->
            when (event) {
                is AddDialogEvent.AddMeal -> {
                    addNavController.navigate(AddScreens.AddMeal.route)
                }
                is AddDialogEvent.ScanFood -> {
                    addNavController.navigate(AddScreens.AddAiMeal.route)
                }
                is AddDialogEvent.AddRecipe -> {
                    addNavController.navigate(AddScreens.AddRecipe.route)
                }
            }
        }
    }

    NavHost(
        navController = addNavController,
        startDestination = startDestination,
    ) {
        dialog(AddScreens.AddMainPage.route) {
            AddDialog(
                onAddMealClick = { addNavController.navigate(AddScreens.AddMeal.route) },
                onAddRecipeClick = { addNavController.navigate(AddScreens.AddRecipe.route) },
                onScanFoodClick = { addNavController.navigate(AddScreens.AddAiMeal.route) },
                onDismissRequest = { mainNavController.popBackStack() }
            )
        }
        composable(AddScreens.AddRecipe.route) { CreateRecipePage() }


        composable(AddScreens.AddMeal.route) { SearchPage() }
        composable(AddScreens.MealOverview.route) { TODO("MealOverviewPage()") }

        composable(AddScreens.AddAiMeal.route) { TODO( "insert addAI meal page" ) }

        composable (AddScreens.FoodOverview.route) { FoodOverview() }
        composable (AddScreens.RecipeOverview.route) { RecipeOverview() }
    }
}

