package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.ui.view.app_views.AddedComponentsSummary
import com.frontend.nutricheck.client.ui.view.app_views.CreateRecipePage
import com.frontend.nutricheck.client.ui.view.app_views.RecipeOverview
import com.frontend.nutricheck.client.ui.view.app_views.RecipePage
import com.frontend.nutricheck.client.ui.view.app_views.SearchPage
import com.frontend.nutricheck.client.ui.view_model.recipe.create.CreateRecipeViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.EditRecipeEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.EditRecipeViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.overview.RecipeOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.page.RecipePageViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.report.ReportRecipeViewModel
import com.frontend.nutricheck.client.ui.view_model.search_food_product.FoodSearchViewModel
import com.frontend.nutricheck.client.ui.view_model.search_food_product.SearchEvent

sealed class RecipePageScreens(val route: String) {
    object RecipePage : RecipePageScreens("recipe_page_route")
    object RecipeOverview : RecipePageScreens("recipe_overview_route/{recipeId}") {
        fun createRoute(recipeId: String) = "recipe_overview_route/$recipeId"
    }
    object FoodProductOverview : RecipePageScreens("food_product_overview_route")
    object CreateRecipePage : RecipePageScreens("create_recipe_page_route")
    object ReportRecipeDialog : RecipePageScreens("report_recipe_dialog_route")
    object ActionConfirmationDialog : RecipePageScreens("action_confirmation_dialog_route")
    object DetailsDialog : RecipePageScreens("details_dialog_route")
    object AddIngredientPage : RecipePageScreens("search/{recipeId}") {
        fun createRoute(recipeId: String) = "search/$recipeId"
    }
    object AddedIngredientSummaryPage : RecipePageScreens("summary/{recipeId}") {
        fun createRoute(recipeId: String) = "summary/$recipeId"
    }
}

@Composable
fun RecipePageNavGraph() {
    val recipePageViewModel : RecipePageViewModel = hiltViewModel()
    val recipePageNavController = rememberNavController()

    NavHost(
        navController = recipePageNavController,
        startDestination = RecipePageScreens.RecipePage.route
    ) {
        composable(RecipePageScreens.RecipePage.route) {
            RecipePage(
                recipePageViewModel = recipePageViewModel
            )
        }

        navigation(
            startDestination = RecipePageScreens.RecipeOverview.route,
            route = "recipe_overview_graph/{recipeId}",
            arguments = listOf(navArgument("recipeId") {
                type = NavType.StringType })
        ) {
            composable(RecipePageScreens.RecipeOverview.route) { backStack ->
                val recipeId = backStack.arguments!!.getString("recipeId")!!
                val recipeOverviewViewModel: RecipeOverviewViewModel = hiltViewModel()
                val editRecipeViewModel: EditRecipeViewModel = hiltViewModel()

                RecipeOverview(
                    recipeOverviewViewModel = recipeOverviewViewModel,
                    editRecipeViewModel = editRecipeViewModel,
                    onAddIngredient = {
                        recipePageNavController.navigate(
                            RecipePageScreens.AddIngredientPage.createRoute(recipeId)
                        )
                    }
                )
            }

            composable(RecipePageScreens.AddIngredientPage.route) { backStack ->
                val searchViewModel: FoodSearchViewModel = hiltViewModel()

                SearchPage(
                    searchViewModel = searchViewModel,
                    onConfirm = {
                        recipePageNavController.navigate(
                            RecipePageScreens.AddedIngredientSummaryPage
                                .createRoute(backStack.arguments!!.getString("reipeId")!!)
                        )
                    }
                )
            }

            composable(RecipePageScreens.AddedIngredientSummaryPage.route) { backStack ->
                val searchViewModel: FoodSearchViewModel = hiltViewModel()
                val editRecipeViewModel: EditRecipeViewModel = hiltViewModel()

                AddedComponentsSummary(
                    searchViewModel = searchViewModel,
                    editRecipeViewModel = editRecipeViewModel,
                    onSave = {
                    }
                )
            }
        }
    }
}