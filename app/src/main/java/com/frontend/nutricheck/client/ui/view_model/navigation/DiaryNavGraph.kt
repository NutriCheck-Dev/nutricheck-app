package com.frontend.nutricheck.client.ui.view_model.navigation

import HistoryPage
import com.frontend.nutricheck.client.ui.view.app_views.RecipePage

package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.frontend.nutricheck.client.ui.view.app_views.CreateRecipePage
import com.frontend.nutricheck.client.ui.view.app_views.FoodOverview
import com.frontend.nutricheck.client.ui.view.app_views.RecipeOverview
import com.frontend.nutricheck.client.ui.view.app_views.SearchPage
import com.frontend.nutricheck.client.ui.view.dialogs.AddDialog
import com.frontend.nutricheck.client.ui.view_model.add_components.AddDialogEvent
import com.frontend.nutricheck.client.ui.view_model.add_components.AddDialogViewModel

sealed class DiaryScreens(val route: String) {
    object HistoryPage : DiaryScreens("history")
    object RecipePage : DiaryScreens("recipe")
    object MealOverview : DiaryScreens("meal_overview")
    object FoodOverview : DiaryScreens("food_overview")
    object RecipeOverview : DiaryScreens("recipe_overview")

}
@Composable
fun DiaryNavGraph(mainNavController: NavHostController) {
    val diaryNavController = rememberNavController()

    //val diaryViewModel: DiaryViewModel = hiltViewModel()

    LaunchedEffect(key1 = Unit) {
//        //diaryViewModel.events.collect { event ->
//            when (event) {
//
//            }
//        }
    }

    NavHost(
        navController = diaryNavController,
        startDestination = AddScreens.AddMainPage.route,
    ) {
        composable(DiaryScreens.HistoryPage.route) { HistoryPage() }
        composable(DiaryScreens.RecipePage.route) { RecipePage() }
        composable(DiaryScreens.RecipeOverview.route) { RecipeOverview() }
        composable(DiaryScreens.FoodOverview.route) { FoodOverview() }
        composable(DiaryScreens.MealOverview.route) { TODO("MealOverviewPage()") }


    }
}