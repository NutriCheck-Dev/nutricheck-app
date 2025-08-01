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
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.ui.view.app_views.AddedComponentsSummary
import com.frontend.nutricheck.client.ui.view.app_views.CreateRecipePage
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.RecipeOverview
import com.frontend.nutricheck.client.ui.view.app_views.SearchPage
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.FoodProductOverview
import com.frontend.nutricheck.client.ui.view.app_views.CameraPreviewScreen
import com.frontend.nutricheck.client.ui.view.dialogs.AddDialog
import com.frontend.nutricheck.client.ui.view_model.add_components.AddAiMealEvent
import com.frontend.nutricheck.client.ui.view_model.food.FoodProductOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.RecipeEditorViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.overview.RecipeOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.report.ReportRecipeViewModel
import com.frontend.nutricheck.client.ui.view_model.search_food_product.FoodSearchViewModel
import com.frontend.nutricheck.client.ui.view_model.search_food_product.SearchEvent

sealed class AddScreens(val route: String) {
    object AddMainPage : Screen("add")
    object AddAiMeal : AddScreens("add_ai_meal/{mealId}")
    object AddMeal : AddScreens("add_meal?fromAddIngredient={fromAddIngredient}") {
        fun createRoute(fromAddIngredient: Boolean): String {
            return "add_meal?fromAddIngredient=$fromAddIngredient"
        }
    }
    object AddRecipe : AddScreens("add_recipe")
    object SearchFoodComponentSummary : AddScreens("search_food_component_summary")
    object HistoryPage : AddScreens("history_page")
    object FoodOverview : AddScreens("food_product_overview/{foodProductId}") {
        fun fromSearch(foodProductId: String) = "food_product_overview/$foodProductId"
        fun fromIngredient(recipeId: String, foodProductId: String) =
            "food_product_overview/$recipeId/$foodProductId"
        fun fromAiMeal(mealId: String, foodProductId: String) = "food_product_overview/$foodProductId/$mealId"
    }
    object RecipeOverview : AddScreens("recipe_overview/{recipeId}") {
        fun createRoute(recipeId: String) = "recipe_overview/$recipeId"
    }

}
@Composable
fun AddNavGraph(mainNavController: NavHostController, origin: AddDialogOrigin) {
    val addNavController = rememberNavController()

    fun navigateToFoodComponent(foodComponent: FoodComponent) {
        if (foodComponent is FoodProduct) {
            addNavController.navigate(AddScreens.FoodOverview.fromSearch(foodComponent.id))
        } else { addNavController.navigate(AddScreens.RecipeOverview.createRoute(foodComponent.id))}
    }

    fun navigateToIngredient(ingredient: Ingredient) {
        addNavController.navigate(
            AddScreens.FoodOverview.fromIngredient(
                ingredient.recipeId,
                ingredient.foodProduct.id))
    }


    NavHost(
        navController = addNavController,
        startDestination = when(origin) {
            AddDialogOrigin.BOTTOM_NAV_BAR -> AddScreens.AddMainPage.route
            AddDialogOrigin.RECIPE_PAGE -> AddScreens.AddRecipe.route
            AddDialogOrigin.HISTORY_PAGE -> AddScreens.AddMeal.createRoute(false)
        },
        route = "add_graph"
    ) {
        composable(AddScreens.AddMainPage.route) {
            AddDialog(
                onAddMealClick = {
                    addNavController.navigate(AddScreens.AddMeal.route)
                                 },
                onAddRecipeClick = {
                    addNavController.navigate(AddScreens.AddRecipe.route)
                                   },
                onScanFoodClick = {
                    addNavController.navigate(AddScreens.AddAiMeal.route)
                                  },
                onDismissRequest = { mainNavController.popBackStack() }
            )
        }
        composable(AddScreens.AddRecipe.route) {
            val createRecipeViewModel: RecipeEditorViewModel = hiltViewModel()
            CreateRecipePage(
                createRecipeViewModel = createRecipeViewModel,
                onItemClick = { ingredient -> navigateToIngredient(ingredient) },
                onAddButtonClick = {
                    addNavController.navigate(AddScreens.AddMeal.createRoute(true))
                                 },
                onSave = {}, //TODO: Implement save functionality
                onBack = { addNavController.popBackStack() }
            )
        }

        composable(AddScreens.AddAiMeal.route) {
            CameraPreviewScreen(
                addAiMealViewModel = hiltViewModel(),
                onNavigateToFoodProductOverview = { mealId, foodProductId ->
                    addNavController.navigate(AddScreens.FoodOverview.fromAiMeal(mealId, foodProductId)) },
                onExit = { mainNavController.popBackStack() })
        }
        composable(
            route = AddScreens.FoodOverview.route,
            arguments = listOf(
                navArgument("foodProductId") { type = NavType.StringType },
                navArgument("mealId") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) {
            backStack ->
            val foodProductId = backStack.arguments!!.getString("foodProductId")!!
            val graphEntry = remember(backStack) {
                addNavController.getBackStackEntry(
                    AddScreens.FoodOverview.fromSearch(foodProductId)
                )
            }
            val searchGraphEntry = remember(backStack) {
                addNavController.getBackStackEntry("add_graph")
            }
            val foodProductOverviewViewModel: FoodProductOverviewViewModel = hiltViewModel(graphEntry)
            val foodSearchViewModel: FoodSearchViewModel = hiltViewModel(searchGraphEntry)
            FoodProductOverview(
                foodProductOverviewViewModel = foodProductOverviewViewModel,
                foodSearchViewModel = foodSearchViewModel,
                onPersist = { addNavController.popBackStack() },
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
                addNavController.getBackStackEntry("add_graph")
            }
            val searchViewModel: FoodSearchViewModel = hiltViewModel(parentEntry)
            SearchPage(
                searchViewModel = searchViewModel,
                onConfirm = { addNavController.navigate(AddScreens.SearchFoodComponentSummary.route)},
                onItemClick = { foodComponent -> navigateToFoodComponent(foodComponent) },
                onBack = { addNavController.popBackStack() }
            )
        }

        composable(AddScreens.SearchFoodComponentSummary.route) { backStackEntry ->
            val searchGraphEntry = remember(backStackEntry) {
                addNavController.getBackStackEntry("add_graph")
            }
            val searchViewModel: FoodSearchViewModel = hiltViewModel(searchGraphEntry)
            AddedComponentsSummary(
                searchViewModel = searchViewModel,
                onItemClick = { foodComponent -> navigateToFoodComponent(foodComponent) },
                onSave = {
                    searchViewModel.onEvent(SearchEvent.SubmitComponents)
                    addNavController.navigate(AddScreens.HistoryPage.route)
                         },
                onBack = { addNavController.popBackStack() }
            )
        }

        composable(AddScreens.HistoryPage.route) { DiaryNavGraph(mainNavController) }

        composable (
            route = AddScreens.FoodOverview.route,
            arguments = listOf(
                navArgument("foodProductId") { type = NavType.StringType },
                navArgument("recipeId") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { backStack ->
            val foodProductId = backStack.arguments!!.getString("foodProductId")!!
            val graphEntry = remember(backStack) {
                addNavController.getBackStackEntry(
                    AddScreens.FoodOverview.fromSearch(foodProductId)
                )
            }
            val searchGraphEntry = remember(backStack) {
                addNavController.getBackStackEntry("add_graph")
            }
            val foodProductOverviewViewModel: FoodProductOverviewViewModel = hiltViewModel(graphEntry)
            val foodSearchViewModel: FoodSearchViewModel = hiltViewModel(searchGraphEntry)

            FoodProductOverview(
                foodProductOverviewViewModel = foodProductOverviewViewModel,
                foodSearchViewModel = foodSearchViewModel,
                onPersist = { addNavController.popBackStack() },
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
            RecipeOverview(
                recipeOverviewViewModel = hiltViewModel(graphEntry),
                editRecipeViewModel = hiltViewModel(graphEntry),
                reportRecipeViewModel = hiltViewModel(graphEntry),
                onBack = { addNavController.popBackStack() }
            )
        }
    }
}

