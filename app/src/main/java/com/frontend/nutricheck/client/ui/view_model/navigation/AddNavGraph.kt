package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.ui.view.app_views.CameraPreviewScreen
import com.frontend.nutricheck.client.ui.view.app_views.CreateMealPage
import com.frontend.nutricheck.client.ui.view.app_views.RecipeEditorPage
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.FoodProductOverview
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.RecipeOverview
import com.frontend.nutricheck.client.ui.view.dialogs.AddDialog
import com.frontend.nutricheck.client.ui.view_model.food.FoodProductOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.edit.RecipeEditorViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.overview.RecipeOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.report.ReportRecipeViewModel
import com.frontend.nutricheck.client.ui.view_model.search_food_product.FoodSearchViewModel

sealed class AddScreens(val route: String) {
    object AddMainPage : Screen("add")
    object AddAiMeal : AddScreens("add_ai_meal/{mealId}")
    object AddMeal : AddScreens("add_meal?recipeId={recipeId}") {
        const val defaultRoute = "add_meal"
    }
    object AddRecipe : AddScreens("add_recipe")
    object HistoryPage : AddScreens("history_page")
    object RecipePage : AddScreens("recipe_page")
    object FoodOverview : AddScreens("food_product_overview/{foodProductId}") {
        fun fromSearch(foodProductId: String) = "food_product_overview/$foodProductId"
        fun fromAiMeal(mealId: String, foodProductId: String) = "food_product_overview/$foodProductId/$mealId"
    }
    object RecipeOverview : AddScreens("recipe_overview/{recipeId}?fromSearch={fromSearch}") {
        fun createRoute(recipeId: String, fromSearch: Boolean) = "recipe_overview/$recipeId?fromSearch=$fromSearch"

    }

}
@Composable
fun AddNavGraph(mainNavController: NavHostController, origin: AddDialogOrigin, date: Long?, dayTime: DayTime?) {
    val addNavController = rememberNavController()

    fun navigateToFoodComponent(foodComponent: FoodComponent) {
        if (foodComponent is FoodProduct) {
            addNavController.navigate(AddScreens.FoodOverview.fromSearch(foodComponent.id))
        } else { addNavController.navigate(AddScreens.RecipeOverview.createRoute(foodComponent.id, true))}
    }

    NavHost(
        navController = addNavController,
        startDestination = when(origin) {
            AddDialogOrigin.BOTTOM_NAV_BAR -> AddScreens.AddMainPage.route
            AddDialogOrigin.RECIPE_PAGE -> AddScreens.AddRecipe.route
            AddDialogOrigin.HISTORY_PAGE -> AddScreens.AddMeal.defaultRoute
        },
        route = "add_graph"
    ) {
        composable(AddScreens.AddMainPage.route) {
            AddDialog(
                onAddMealClick = {
                    addNavController.navigate(AddScreens.AddMeal.defaultRoute)
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

        composable(route = AddScreens.AddMeal.defaultRoute) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                addNavController.getBackStackEntry("add_graph")
            }
            LaunchedEffect(Unit) {
                date?.let {
                    if (parentEntry.savedStateHandle.get<String>("date") == null) {
                        parentEntry.savedStateHandle["date"] = it
                    }
                }
                dayTime?.let {
                    if (parentEntry.savedStateHandle.get<String>("dayTime") == null) {
                        parentEntry.savedStateHandle["dayTime"] = it
                    }
                }
            }
            val searchViewModel: FoodSearchViewModel = hiltViewModel(parentEntry)
            CreateMealPage(
                searchViewModel = searchViewModel,
                onConfirm = { addNavController.navigate(AddScreens.HistoryPage.route)},
                onItemClick = { foodComponent -> navigateToFoodComponent(foodComponent) },
                onBack = { mainNavController.popBackStack() }
            )
        }

        composable(
            route = AddScreens.AddRecipe.route,
            arguments = listOf(
                navArgument("recipeId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStack ->
            val searchGraphEntry = remember(backStack) {
                addNavController.getBackStackEntry("add_graph")
            }
            val createRecipeViewModel: RecipeEditorViewModel = hiltViewModel(searchGraphEntry)
            RecipeEditorPage(
                createRecipeViewModel = createRecipeViewModel,
                onItemClick = { foodComponent -> navigateToFoodComponent(foodComponent) },
                onSave = { addNavController.navigate(AddScreens.RecipePage.route)},
                onBack = { addNavController.popBackStack() }
            )
        }

        composable(AddScreens.RecipePage.route) { RecipePageNavGraph(mainNavController)}

        composable(AddScreens.HistoryPage.route) { DiaryNavGraph(mainNavController) }

        composable (
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

        composable (
            route = AddScreens.RecipeOverview.route,
            arguments = listOf(
                navArgument("recipeId") { type = NavType.StringType },
                navArgument("fromSearch") { type = NavType.BoolType }
            )
        ) { backStack ->
            val recipeId = backStack.arguments!!.getString("recipeId")!!
            val fromSearch = backStack.arguments!!.getBoolean("fromSearch")
            val graphEntry = remember(backStack) {
                addNavController.getBackStackEntry(
                    AddScreens.RecipeOverview.createRoute(recipeId, fromSearch)
                )
            }
            val searchGraphEntry = remember(backStack) {
                addNavController.getBackStackEntry("add_graph")
            }
            val recipeOverviewViewModel: RecipeOverviewViewModel = hiltViewModel(graphEntry)
            val reportRecipeViewModel: ReportRecipeViewModel = hiltViewModel(graphEntry)
            val searchViewModel: FoodSearchViewModel = hiltViewModel(searchGraphEntry)
            RecipeOverview(
                recipeOverviewViewModel = recipeOverviewViewModel,
                reportRecipeViewModel = reportRecipeViewModel,
                searchViewModel = searchViewModel,
                onPersist = { addNavController.popBackStack() },
                onBack = { addNavController.popBackStack() }
            )
        }
    }
}

