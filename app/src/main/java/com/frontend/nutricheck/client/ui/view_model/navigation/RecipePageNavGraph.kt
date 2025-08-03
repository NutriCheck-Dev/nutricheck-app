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
import com.frontend.nutricheck.client.ui.view.app_views.CreateMealPage
import com.frontend.nutricheck.client.ui.view.app_views.CreateRecipePage
import com.frontend.nutricheck.client.ui.view.app_views.RecipePage
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.FoodProductOverview
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.RecipeOverview
import com.frontend.nutricheck.client.ui.view_model.food.FoodProductOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.RecipeEditorViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.overview.RecipeOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.page.RecipePageViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.report.ReportRecipeViewModel
import com.frontend.nutricheck.client.ui.view_model.search_food_product.FoodSearchViewModel

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
fun RecipePageNavGraph(
    mainNavGraph: NavHostController
) {
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
            val reportRecipeViewModel: ReportRecipeViewModel = hiltViewModel()
            RecipePage(
                recipePageViewModel = recipePageViewModel,
                reportRecipeViewModel = reportRecipeViewModel,
                onAddRecipeClick = {
                    recipePageNavController.navigate(RecipePageScreens.CreateRecipePage.route)
                },
                onItemClick = { foodComponent ->
                    navigateToFoodComponent(foodComponent)
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
            val editRecipeViewModel: RecipeEditorViewModel = hiltViewModel(graphEntry)

            RecipeOverview(
                recipeOverviewViewModel = recipeOverviewViewModel,
                editRecipeViewModel = editRecipeViewModel,
                /**onItemClick = { foodComponent ->
                    navigateToFoodComponent(foodComponent)
                },**/
            )
        }
        composable(
            RecipePageScreens.AddIngredientPage.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStack ->
            val recipeId = backStack.arguments!!.getString("recipeId")!!
            val graphEntry = remember(backStack) {
                recipePageNavController.getBackStackEntry(
                    RecipePageScreens.RecipeOverview.createRoute(recipeId)
                )
            }
            val editRecipeViewModel: RecipeEditorViewModel = hiltViewModel(graphEntry)

            CreateMealPage(
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
        composable(RecipePageScreens.CreateRecipePage.route) {
            val createRecipeViewModel: RecipeEditorViewModel = hiltViewModel()
            val searchViewModel: FoodSearchViewModel = hiltViewModel()
            CreateRecipePage(
                createRecipeViewModel = createRecipeViewModel,
                onItemClick = { foodComponent ->
                    navigateToFoodComponent(foodComponent)
                },
                onBack = { recipePageNavController.popBackStack() },
                onSave = {
                    recipePageNavController.navigate(RecipePageScreens.RecipePage.route) {
                        popUpTo(RecipePageScreens.RecipePage.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            RecipePageScreens.FoodProductOverview.route,
            arguments = listOf(navArgument("foodId") { type = NavType.StringType })
        ) { backStack ->
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
