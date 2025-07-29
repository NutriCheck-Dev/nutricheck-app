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
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.ui.view.app_views.AddedComponentsSummary
import com.frontend.nutricheck.client.ui.view.app_views.CreateRecipePage
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.RecipeOverview
import com.frontend.nutricheck.client.ui.view.app_views.RecipePage
import com.frontend.nutricheck.client.ui.view.app_views.SearchPage
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.FoodProductOverview
import com.frontend.nutricheck.client.ui.view_model.food.FoodProductOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.create.CreateRecipeViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.EditRecipeViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.overview.RecipeOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.page.RecipePageViewModel

sealed class RecipePageScreens(val route: String) {
    object RecipePage : RecipePageScreens("recipe_page")
    object RecipeOverview : RecipePageScreens("recipe_overview/{recipeId}") {
        fun createRoute(recipeId: String) = "recipe_overview/$recipeId"
    }
    object FoodProductOverview : RecipePageScreens("food_product_overview/{foodId}") {
        fun createRoute(foodId: String) = "food_product_overview/$foodId"
    }
    object CreateRecipePage : RecipePageScreens("create_recipe_page_route")
    object ReportRecipeDialog : RecipePageScreens("report_recipe_dialog_route")
    object ActionConfirmationDialog : RecipePageScreens("action_confirmation_dialog_route")
    object AddIngredientPage : RecipePageScreens("search/{recipeId}") {
        fun createRoute(recipeId: String) = "search/$recipeId"
    }
    object AddedIngredientSummaryPage : RecipePageScreens("summary/{recipeId}") {
        fun createRoute(recipeId: String) = "summary/$recipeId"
    }
}

@Composable
fun RecipePageNavGraph() {
    val recipePageNavController = rememberNavController()

    fun navigateToFoodComponent(foodComponent: FoodComponent) {
        if (foodComponent is FoodProduct) {
            recipePageNavController.navigate(
                RecipePageScreens.FoodProductOverview.createRoute(foodComponent.id)
            )
        } else {
            recipePageNavController.navigate(
                RecipePageScreens.RecipeOverview.createRoute(foodComponent.id)
            )
        }
    }

    NavHost(
        navController = recipePageNavController,
        startDestination = RecipePageScreens.RecipePage.route
    ) {
        composable(RecipePageScreens.RecipePage.route) {
            val recipePageViewModel : RecipePageViewModel = hiltViewModel()
            RecipePage(
                recipePageViewModel = recipePageViewModel,
                onAddRecipeClick = {
                    recipePageNavController.navigate(
                        RecipePageScreens.CreateRecipePage.route
                    )
                },
                onItemClick = { recipe ->
                    navigateToFoodComponent(recipe)
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
                val recipeId = backStack.arguments!!.getString("123")!!
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
                    onItemClick = { foodComponent ->
                        navigateToFoodComponent(foodComponent)
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
                    //editRecipeViewModel = editRecipeViewModel,
                    searchViewModel = hiltViewModel(),
                    onItemClick = { foodComponent ->
                        navigateToFoodComponent(foodComponent)
                    },
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
                    searchViewModel = hiltViewModel(),
                    onItemClick = { foodComponent ->
                        navigateToFoodComponent(foodComponent)
                    },
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

            composable(RecipePageScreens.CreateRecipePage.route) {
                val createRecipeViewModel: CreateRecipeViewModel = hiltViewModel()
                CreateRecipePage(
                    createRecipeViewModel = createRecipeViewModel,
                    onItemClick = { foodComponent ->
                        navigateToFoodComponent(foodComponent)
                    },
                    onBack = { recipePageNavController.popBackStack() },
                    onSave = {
                        recipePageNavController.navigate(RecipePageScreens.RecipePage.route) {
                            popUpTo(RecipePageScreens.RecipePage.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(RecipePageScreens.FoodProductOverview.route) { backStack ->
                val foodId = backStack.arguments!!.getString("foodId")!!
                val graphEntry = remember(backStack) {
                    recipePageNavController.getBackStackEntry(
                        RecipePageScreens.FoodProductOverview.createRoute(foodId)
                    )
                }
                val foodProductOverviewViewModel: FoodProductOverviewViewModel = hiltViewModel(graphEntry)

                FoodProductOverview(
                    foodProductOverviewViewModel = foodProductOverviewViewModel,
                    onBack = { recipePageNavController.popBackStack() }
                )
            }
        }
    }
}