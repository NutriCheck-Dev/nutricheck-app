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
import com.frontend.nutricheck.client.ui.view.app_views.AddedComponentsSummary
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.RecipeOverview
import com.frontend.nutricheck.client.ui.view.app_views.RecipePage
import com.frontend.nutricheck.client.ui.view.app_views.SearchPage
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.EditRecipeViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.overview.RecipeOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.page.RecipePageViewModel

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
                recipePageViewModel = recipePageViewModel,
                onItemClick = { recipeId ->
                    recipePageNavController.navigate(
                        RecipePageScreens.RecipeOverview.createRoute(recipeId)
                    )
                }
            )
        }

        navigation(
            startDestination = RecipePageScreens.RecipeOverview.route,
            route = RecipePageScreens.RecipeOverview.route,
            arguments = listOf(navArgument("recipeId") {
                type = NavType.StringType })
        ) {
            composable(RecipePageScreens.RecipeOverview.route) { backStack ->
                val recipeId = backStack.arguments!!.getString("recipeId")!!
                val graphEntry = remember(backStack) {
                    recipePageNavController.getBackStackEntry(
                        RecipePageScreens.RecipeOverview.createRoute(recipeId)
                    )
                }
                val recipeOverviewViewModel: RecipeOverviewViewModel = hiltViewModel(graphEntry)
                val editRecipeViewModel: EditRecipeViewModel = hiltViewModel(graphEntry)

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
                val recipeId = backStack.arguments!!.getString("recipeId")!!
                val graphEntry = remember(backStack) {
                    recipePageNavController.getBackStackEntry(
                        RecipePageScreens.RecipeOverview.createRoute(recipeId)
                    )
                }
                val editRecipeViewModel: EditRecipeViewModel = hiltViewModel(graphEntry)

                SearchPage(
                    editRecipeViewModel = editRecipeViewModel,
                    onConfirm = {
                        recipePageNavController.navigate(
                            RecipePageScreens.AddedIngredientSummaryPage.createRoute(recipeId)
                        )
                    }
                )
            }

            composable(RecipePageScreens.AddedIngredientSummaryPage.route) { backStack ->
                val recipeId = backStack.arguments!!.getString("recipeId")!!
                val graphEntry = remember(backStack) {
                    recipePageNavController.getBackStackEntry(
                        RecipePageScreens.RecipeOverview.createRoute(recipeId)
                    )
                }
                val editRecipeViewModel: EditRecipeViewModel = hiltViewModel(graphEntry)
                AddedComponentsSummary(
                    editRecipeViewModel = editRecipeViewModel,
                    onSave = {
                        recipePageNavController.navigate(
                            RecipePageScreens.RecipeOverview.createRoute(recipeId)
                        ) {
                            popUpTo(
                                RecipePageScreens.RecipeOverview.createRoute(recipeId)
                            ) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    },
                    onBack = {
                        recipePageNavController.popBackStack()
                    }
                )
            }
        }
    }
}