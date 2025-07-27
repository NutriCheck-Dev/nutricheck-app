package com.frontend.nutricheck.client.ui.view_model.navigation

import com.frontend.nutricheck.client.ui.view.app_views.HistoryPage
import com.frontend.nutricheck.client.ui.view_model.history.HistoryViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.page.RecipePageViewModel
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.frontend.nutricheck.client.ui.view.app_views.DiaryPage


sealed class DiaryScreens(val route: String) {
    object HistoryPage : DiaryScreens("history_page")
    object RecipePage : DiaryScreens("recipe_page")
    object DiaryPage : DiaryScreens("diary_page")

}
@Composable
fun DiaryNavGraph() {
    val diaryNavController = rememberNavController()
    val historyViewModel : HistoryViewModel = hiltViewModel()



    NavHost(
        navController = diaryNavController,
        startDestination = DiaryScreens.DiaryPage.route,
    ) {
        composable(DiaryScreens.HistoryPage.route) { HistoryPage(historyViewModel = historyViewModel) }
        composable(DiaryScreens.RecipePage.route) { RecipePageNavGraph() }
        composable(DiaryScreens.DiaryPage.route) {
            val recipePageViewModel: RecipePageViewModel = hiltViewModel()
            val historyViewModel : HistoryViewModel = hiltViewModel()
            DiaryPage(historyViewModel = historyViewModel, recipePageViewModel = recipePageViewModel)
        }


    }
}