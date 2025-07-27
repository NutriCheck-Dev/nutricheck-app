package com.frontend.nutricheck.client.ui.view_model.navigation

import com.frontend.nutricheck.client.ui.view.app_views.HistoryPage
import com.frontend.nutricheck.client.ui.view.app_views.RecipePage
import com.frontend.nutricheck.client.ui.view_model.history.HistoryViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.page.RecipePageViewModel
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.frontend.nutricheck.client.ui.view.app_views.DiaryPage
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.FoodProductOverview
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.RecipeOverview


sealed class DiaryScreens(val route: String) {
    object HistoryPage : DiaryScreens("history")
    object RecipePage : DiaryScreens("recipe")
    object MealOverview : DiaryScreens("meal_overview")
    object FoodOverview : DiaryScreens("food_overview")
    object RecipeOverview : DiaryScreens("recipe_overview")
    object DiaryPage : DiaryScreens("diary_page")

}
@Composable
fun DiaryNavGraph() {
    val diaryNavController = rememberNavController()

    val recipePageViewModel: RecipePageViewModel = hiltViewModel()
    val historyViewModel : HistoryViewModel = hiltViewModel()



    NavHost(
        navController = diaryNavController,
        startDestination = DiaryScreens.DiaryPage.route,
    ) {
        composable(DiaryScreens.HistoryPage.route) { HistoryPage(historyViewModel = historyViewModel) }
        composable(DiaryScreens.RecipePage.route) { RecipePage(recipePageViewModel = recipePageViewModel) }
        composable(DiaryScreens.DiaryPage.route) { DiaryPage(historyViewModel = historyViewModel, recipePageViewModel = recipePageViewModel) }
        composable(DiaryScreens.RecipeOverview.route) { RecipeOverview() }
        composable(DiaryScreens.FoodOverview.route) { FoodProductOverview(foodProductOverviewViewModel = hiltViewModel()) }
        composable(DiaryScreens.MealOverview.route) {  }


    }
}