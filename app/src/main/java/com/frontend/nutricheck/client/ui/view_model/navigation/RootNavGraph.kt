package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.frontend.nutricheck.client.ui.view.app_views.DiaryPage
import com.frontend.nutricheck.client.ui.view.app_views.HomePage
import com.frontend.nutricheck.client.ui.view.app_views.ProfilePage

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object HomePage : Screen("home")
    data object DiaryPage : Screen("diary")
    data object ProfilePage : Screen("profile")
    data object Add : Screen("add")

    data object RecipePage : Screen("diary/recipe")
    data object HistoryPage : Screen("diary/history")
    data object RecipeOverview : Screen("recipe_overview")
    data object DishItemOverview : Screen("dish_item_overview/{dishId}") {
        fun createRoute(dishId: String) = "dish_item_overview/$dishId"
    }
}
    @Composable
fun RootNavGraph(mainNavController: NavHostController, startDestination: String) {
    NavHost(
        navController = mainNavController,
        startDestination = startDestination,
    ) {
        composable(Screen.Onboarding.route) { OnboardingNavGraph(mainNavController) }

        composable(Screen.HomePage.route) { HomePage() }
        composable(Screen.DiaryPage.route) { DiaryPage() }
        composable(Screen.ProfilePage.route) { ProfilePageNavGraph() }
        dialog(Screen.Add.route) { AddNavGraph(mainNavController) }


        composable(Screen.RecipePage.route) {  }
        composable(Screen.HistoryPage.route) {  }
        composable(Screen.DishItemOverview.route) {  }
        composable(Screen.RecipeOverview.route) {  }
    }
}