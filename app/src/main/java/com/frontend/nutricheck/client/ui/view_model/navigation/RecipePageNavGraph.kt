package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.frontend.nutricheck.client.ui.view_model.food.FoodProductOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeEditorViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeOverviewEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipePageEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipePageViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.ReportRecipeViewModel
import com.frontend.nutricheck.client.ui.view_model.food.FoodProductOverviewEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeEditorEvent

sealed class RecipePageScreens(val route: String) {
    object RecipePage : RecipePageScreens("recipe_page")
    object RecipeOverview : RecipePageScreens("recipe_overview/{recipeId}") {
        fun createRoute(recipeId: String) = "recipe_overview/$recipeId"
    }
    object FoodProductOverview : AddScreens("food_product_overview/{foodProductId}?recipeId={recipeId}") {
        fun fromSearch(foodProductId: String) = "food_product_overview/$foodProductId"
        fun fromIngredient(recipeId: String, foodProductId: String) = "food_product_overview/$foodProductId?recipeId=$recipeId"
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
        startDestination = "recipe_page_graph"
    ) {

        navigation(
            startDestination = RecipePageScreens.RecipePage.route,
            route = "recipe_page_graph"
        ) {
            composable(RecipePageScreens.RecipePage.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    recipePageNavController.getBackStackEntry("recipe_page_graph")
                }
                val recipePageViewModel: RecipePageViewModel = hiltViewModel(parentEntry)
                val reportRecipeViewModel: ReportRecipeViewModel = hiltViewModel(parentEntry)
                LaunchedEffect(recipePageViewModel) {
                    recipePageViewModel.events.collect { event ->
                        if (event is RecipePageEvent.NavigateToEditRecipe) {
                            recipePageNavController.navigate(
                                RecipePageScreens.RecipeEditorPage.editRecipe(event.recipeId)
                            )
                        }

                    }
                }
                RecipePage(
                    recipePageViewModel = recipePageViewModel,
                    reportRecipeViewModel = reportRecipeViewModel,
                    onAddRecipeClick = {
                        recipePageNavController.navigate(RecipePageScreens.RecipeEditorPage.NEW_RECIPE)
                    },
                    onItemClick = { recipe ->
                        recipePageNavController.navigate(
                            RecipePageScreens.RecipeOverview.createRoute(
                                recipe.id
                            )
                        )
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
                LaunchedEffect(recipeOverviewViewModel) {
                    recipeOverviewViewModel.events.collect { event ->
                        if (event is RecipeOverviewEvent.NavigateToEditRecipe) {
                            recipePageNavController.navigate(
                                RecipePageScreens.RecipeEditorPage.editRecipe(event.recipeId)
                            )
                        }

                    }
                }
                RecipeOverview(
                    recipeOverviewViewModel = recipeOverviewViewModel,
                    reportRecipeViewModel = reportRecipeViewModel,
                    onItemClick = { ingredient ->
                        recipePageNavController.navigate(
                            RecipePageScreens.FoodProductOverview.fromIngredient(
                                recipeId,
                                ingredient.foodProduct.id
                            )
                        )
                    },
                    onPersist = { recipePageNavController.popBackStack() },
                    onBack = { recipePageNavController.popBackStack() }
                )
            }
        }

        composable(
            route = AddScreens.FoodOverview.route,
            arguments = listOf(
                navArgument("foodProductId") { type = NavType.StringType },
                navArgument("recipeId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStack ->
            val foodProductId = backStack.arguments!!.getString("foodProductId")!!
            val recipeId = backStack.arguments?.getString("recipeId")!!
            val graphEntry = remember(backStack) {
                recipePageNavController.getBackStackEntry(
                    AddScreens.FoodOverview.fromIngredient(recipeId, foodProductId)
                )
            }
            val foodProductOverviewViewModel: FoodProductOverviewViewModel =
                hiltViewModel(graphEntry)

            LaunchedEffect(foodProductOverviewViewModel) {
                foodProductOverviewViewModel.events.collect { event ->
                    if (event is FoodProductOverviewEvent.UpdateIngredient) {
                        recipePageNavController.popBackStack()
                    }
                }
            }
            FoodProductOverview(
                foodProductOverviewViewModel = foodProductOverviewViewModel,
                onBack = { recipePageNavController.popBackStack() }
            )
        }

        navigation(
            startDestination = RecipePageScreens.RecipeEditorPage.route,
            route = "recipe_editor_page_graph"
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
                val parentEntry = remember(backStackEntry) {
                    recipePageNavController.getBackStackEntry("recipe_editor_page_graph")
                }
                val recipeEditorViewModel: RecipeEditorViewModel = hiltViewModel(parentEntry)

                LaunchedEffect(recipeEditorViewModel) {
                    recipeEditorViewModel.events.collect { event ->
                        if (event is RecipeEditorEvent.RecipeSaved) {
                            recipePageNavController.popBackStack()
                        }
                    }
                }

                RecipeEditorPage(
                    recipeEditorViewModel = recipeEditorViewModel,
                    onItemClick = { foodProduct ->
                        recipePageNavController.navigate(
                            RecipePageScreens.FoodProductOverview.fromSearch(foodProduct.id))
                    },
                    onBack = { recipePageNavController.popBackStack() }
                )
            }

            composable(
                route = RecipePageScreens.FoodProductOverview.route,
                arguments = listOf(
                    navArgument("foodProductId") { type = NavType.StringType },
                )
            ) { backStack ->
                val foodProductId = backStack.arguments!!.getString("foodProductId")!!
                val graphEntry = remember(backStack) {
                    recipePageNavController.getBackStackEntry(
                        RecipePageScreens.FoodProductOverview.fromSearch(foodProductId)
                    )
                }
                val parentEntry = remember(graphEntry) {
                    recipePageNavController.getBackStackEntry("recipe_editor_page_graph")
                }
                val foodProductOverviewViewModel: FoodProductOverviewViewModel =
                    hiltViewModel(graphEntry)
                val recipeEditorViewModel: RecipeEditorViewModel = hiltViewModel(parentEntry)

                LaunchedEffect(recipeEditorViewModel) {
                    recipeEditorViewModel.events.collect { event ->
                        if (event is RecipeEditorEvent.IngredientAdded) {
                            recipePageNavController.popBackStack()
                        }
                    }
                }
                FoodProductOverview(
                    foodProductOverviewViewModel = foodProductOverviewViewModel,
                    recipeEditorViewModel = recipeEditorViewModel,
                    onBack = { recipePageNavController.popBackStack() }
                )
            }

        }
    }
}
