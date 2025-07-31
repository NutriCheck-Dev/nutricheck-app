package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.ui.view.app_views.AddedComponentsSummary
import com.frontend.nutricheck.client.ui.view.app_views.CreateRecipePage
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.RecipeOverview
import com.frontend.nutricheck.client.ui.view.app_views.SearchPage
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.FoodProductOverview
import com.frontend.nutricheck.client.ui.view.dialogs.AddDialog
import com.frontend.nutricheck.client.ui.view_model.food.FoodProductOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.RecipeEditorViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.overview.RecipeOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.report.ReportRecipeViewModel
import com.frontend.nutricheck.client.ui.view_model.search_food_product.FoodSearchViewModel

sealed class AddScreens(val route: String) {
    object AddMainPage : Screen("add")
    object AddAiMeal : AddScreens("add_ai_meal")
    object AddMeal : AddScreens("add_meal?fromAddIngredient={fromAddIngredient}") {
        fun createRoute(fromAddIngredient: Boolean): String {
            return "add_meal?fromAddIngredient=$fromAddIngredient"
        }
    }
    object AddRecipe : AddScreens("add_recipe")
    object MealOverview : AddScreens("meal_overview")
    object HistoryPage : AddScreens("history_page")
    object FoodOverview : AddScreens("food_product_overview/{foodId}") {
        fun createRoute(foodId: String) = "food_product_overview/$foodId"
    }
    object RecipeOverview : AddScreens("recipe_overview/{recipeId}") {
        fun createRoute(recipeId: String) = "recipe_overview/$recipeId"
    }

}
@Composable
fun AddNavGraph(mainNavController: NavHostController, origin: AddDialogOrigin) {
    val addNavController = rememberNavController()
    val createRecipeViewModel: RecipeEditorViewModel = hiltViewModel()

    fun navigateToFoodComponent(foodComponent: FoodComponent) {
        if (foodComponent is FoodProduct) { addNavController.navigate(AddScreens.FoodOverview.createRoute(foodComponent.id))
        } else { addNavController.navigate(AddScreens.RecipeOverview.createRoute(foodComponent.id))}
    }

    NavHost(
        navController = addNavController,
        startDestination = when(origin) {
            AddDialogOrigin.BOTTOM_NAV_BAR -> AddScreens.AddMainPage.route
            AddDialogOrigin.RECIPE_PAGE -> AddScreens.AddRecipe.route
            AddDialogOrigin.HISTORY_PAGE -> AddScreens.AddMeal.createRoute(false)
        },
    ) {
        composable(AddScreens.AddMainPage.route) {
            AddDialog(
                onAddMealClick = { addNavController.navigate(AddScreens.AddMeal.route) },
                onAddRecipeClick = { addNavController.navigate(AddScreens.AddRecipe.route) },
                onScanFoodClick = { addNavController.navigate(AddScreens.AddAiMeal.route) },
                onDismissRequest = { mainNavController.popBackStack() }
            )
        }
        composable(AddScreens.AddRecipe.route) {
            CreateRecipePage(
                createRecipeViewModel = createRecipeViewModel,
                //onItemClick = { foodComponent -> navigateToFoodComponent(foodComponent) },
                onBack = { addNavController.popBackStack() }
            )
        }


        composable(
            route = AddScreens.AddMeal.route,
            arguments = listOf(
                navArgument("fromAddIngredient") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                addNavController.getBackStackEntry(AddScreens.AddMeal.route)
            }
            val searchViewModel: FoodSearchViewModel = hiltViewModel(parentEntry)
            SearchPage(
                searchViewModel = searchViewModel,
                onConfirm = { addNavController.navigate(AddScreens.MealOverview.route)},
                onItemClick = { foodComponent -> navigateToFoodComponent(foodComponent) },
                onBack = { addNavController.popBackStack() }
            )
        }
        composable(AddScreens.MealOverview.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                addNavController.getBackStackEntry(AddScreens.AddMeal.route)
            }
            val searchViewModel: FoodSearchViewModel = hiltViewModel(parentEntry)
            AddedComponentsSummary(
                searchViewModel = searchViewModel,
                onItemClick = { foodComponent -> navigateToFoodComponent(foodComponent) },
                onSave = { addNavController.navigate(AddScreens.HistoryPage.route) },
                onBack = { addNavController.popBackStack() }
            )
        }

        composable(AddScreens.AddAiMeal.route) {  }

        composable (AddScreens.FoodOverview.route) { backStack ->
            val foodId = backStack.arguments!!.getString("foodId")!!
            val graphEntry = remember(backStack) {
                addNavController.getBackStackEntry(
                    AddScreens.FoodOverview.createRoute(foodId)
                )
            }
            val foodProductOverviewViewModel: FoodProductOverviewViewModel = hiltViewModel(graphEntry)
            FoodProductOverview(
                foodProductOverviewViewModel = foodProductOverviewViewModel,
                onBack = { addNavController.popBackStack() }
            )
        }
        composable (AddScreens.RecipeOverview.route) { backStack ->
            val recipeId = backStack.arguments!!.getString("recipeId")!!
            val graphEntry = remember(backStack) {
                addNavController.getBackStackEntry(
                    AddScreens.RecipeOverview.createRoute(recipeId)
                )
            }
            val recipeOverviewViewModel: RecipeOverviewViewModel = hiltViewModel(graphEntry)
            val editRecipeViewModel: RecipeEditorViewModel = hiltViewModel(graphEntry)
            val reportRecipeViewModel: ReportRecipeViewModel = hiltViewModel(graphEntry)
            RecipeOverview(
                recipeOverviewViewModel = recipeOverviewViewModel,
                editRecipeViewModel = editRecipeViewModel,
                reportRecipeViewModel = reportRecipeViewModel,
                onBack = { addNavController.popBackStack() }
            )
        }
    }
}

