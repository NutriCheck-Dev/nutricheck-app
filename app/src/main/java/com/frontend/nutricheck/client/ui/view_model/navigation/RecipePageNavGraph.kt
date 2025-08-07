package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.frontend.nutricheck.client.ui.view.app_views.RecipeEditorPage
import com.frontend.nutricheck.client.ui.view.app_views.RecipePage
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.FoodProductOverview
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.RecipeOverview
import com.frontend.nutricheck.client.ui.view_model.food.FoodProductOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeEditorViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipePageViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.ReportRecipeViewModel
import com.frontend.nutricheck.client.ui.view_model.search_food_component.FoodSearchViewModel

sealed class RecipePageScreens(val route: String) {
    object RecipePage : RecipePageScreens("recipe_page")
    object RecipeOverview : RecipePageScreens("recipe_overview/{recipeId}") {
        fun createRoute(recipeId: String) = "recipe_overview/$recipeId"
    }
    object FoodProductOverview : AddScreens("food_product_overview/{foodProductId}?recipeId={recipeId}") {
        fun fromIngredient(recipeId: String, foodProductId: String) = "food_product_overview/$foodProductId?recipeId=$recipeId"
    }
    object CreateRecipePage : RecipePageScreens("create_recipe_page_route")
    object AddedIngredientSummaryPage : RecipePageScreens("summary/{recipeId}") {
        fun createRoute(recipeId: String) = "summary/$recipeId"
    }
}

@Composable
fun RecipePageNavGraph(
    mainNavGraph: NavHostController
) {
    val recipePageNavController = rememberNavController()

    NavHost(
        navController = recipePageNavController,
        startDestination = RecipePageScreens.RecipePage.route
    ) {
        composable(RecipePageScreens.RecipePage.route) {
            val recipePageViewModel : RecipePageViewModel = hiltViewModel()
            val reportRecipeViewModel: ReportRecipeViewModel = hiltViewModel()
            RecipePage(
                recipePageViewModel = recipePageViewModel,
                reportRecipeViewModel = reportRecipeViewModel,
                onAddRecipeClick = {
                    recipePageNavController.navigate(RecipePageScreens.CreateRecipePage.route)
                },
                onItemClick = { recipe ->
                    recipePageNavController.navigate(RecipePageScreens.RecipeOverview.createRoute(recipe.id))
                }
            )
        }
        composable(
            RecipePageScreens.RecipeOverview.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStack ->
            val recipeId = backStack.arguments!!.getString("recipeId")!!
            val graphEntry = remember(backStack) {
                recipePageNavController.getBackStackEntry(
                    RecipePageScreens.RecipeOverview.createRoute(recipeId)
                )
            }
            val recipeOverviewViewModel: RecipeOverviewViewModel = hiltViewModel(graphEntry)
            val reportRecipeViewModel: ReportRecipeViewModel = hiltViewModel(graphEntry)
            val searchViewModel: FoodSearchViewModel = hiltViewModel(graphEntry)
            RecipeOverview(
                recipeOverviewViewModel = recipeOverviewViewModel,
                reportRecipeViewModel = reportRecipeViewModel,
                searchViewModel = searchViewModel,
                onBack = { recipePageNavController.popBackStack() }
            )
        }

        composable(RecipePageScreens.CreateRecipePage.route) {
            val recipeEditorViewModel: RecipeEditorViewModel = hiltViewModel()
            val recipeEditorState by recipeEditorViewModel.draft.collectAsState()
            RecipeEditorPage(
                recipeEditorViewModel = recipeEditorViewModel,
                onItemClick = { foodProduct ->
                    recipePageNavController.navigate(RecipePageScreens.FoodProductOverview.fromIngredient(recipeEditorState.id, foodProduct.id))
                },
                onBack = { recipePageNavController.popBackStack() },
                onSave = {
                    recipePageNavController.popBackStack()
                    /**recipePageNavController.navigate(RecipePageScreens.RecipePage.route) {
                        popUpTo(RecipePageScreens.RecipePage.route) { inclusive = true }
                    }**/
                }
            )
        }
        composable(
            RecipePageScreens.FoodProductOverview.route,
            arguments = listOf(
                navArgument("foodId") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("recipeId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStack ->
            val foodId = backStack.arguments!!.getString("foodId")!!
            val recipeId = backStack.arguments!!.getString("recipeId")!!
            val graphEntry = remember(backStack) {
                recipePageNavController.getBackStackEntry(
                    RecipePageScreens.FoodProductOverview.fromIngredient(recipeId, foodId)
                )
            }
            val foodProductOverviewViewModel: FoodProductOverviewViewModel = hiltViewModel(graphEntry)

            FoodProductOverview(
                foodProductOverviewViewModel = foodProductOverviewViewModel,
                onPersist = { recipePageNavController.popBackStack() },
                onBack = { recipePageNavController.popBackStack() }
            )
        }
    }
}
