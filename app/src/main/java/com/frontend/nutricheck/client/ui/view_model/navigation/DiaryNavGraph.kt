package com.frontend.nutricheck.client.ui.view_model.navigation

import HistoryPage
import com.frontend.nutricheck.client.ui.view.app_views.RecipePage
import com.frontend.nutricheck.client.ui.view_model.history.HistoryViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.page.RecipePageViewModel
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.frontend.nutricheck.client.ui.view.app_views.FoodOverview
import com.frontend.nutricheck.client.ui.view.app_views.RecipeOverview


sealed class DiaryScreens(val route: String) {
    object HistoryPage : DiaryScreens("history")
    object RecipePage : DiaryScreens("recipe")
    object MealOverview : DiaryScreens("meal_overview")
    object FoodOverview : DiaryScreens("food_overview")
    object RecipeOverview : DiaryScreens("recipe_overview")

}
@Composable
fun DiaryNavGraph() {
    val diaryNavController = rememberNavController()

    val recipePageViewModel: RecipePageViewModel = hiltViewModel()
    val historyViewModel : HistoryViewModel = hiltViewModel()



    NavHost(
        navController = diaryNavController,
        startDestination = AddScreens.AddMainPage.route,
    ) {
        composable(DiaryScreens.HistoryPage.route) { HistoryPage(historyViewModel) }
        composable(DiaryScreens.RecipePage.route) { RecipePage(recipePageViewModel) }
        composable(DiaryScreens.RecipeOverview.route) { RecipeOverview() }
        composable(DiaryScreens.FoodOverview.route) { FoodOverview() }
        composable(DiaryScreens.MealOverview.route) { TODO("MealOverviewPage()") }


    }
}