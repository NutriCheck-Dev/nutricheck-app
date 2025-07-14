package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.frontend.nutricheck.client.ui.view.app_views.DiaryPage
import com.frontend.nutricheck.client.ui.view.app_views.HomePage
import com.frontend.nutricheck.client.ui.view.app_views.ProfilePage

@Composable
fun RootNavGraph(mainNavController: NavHostController, startDestination: String) {
    NavHost(
        navController = mainNavController,
        startDestination = startDestination,
    ) {
        composable(Screen.Onboarding.route) { OnboardingNavGraph(mainNavController) }
        composable(Screen.HomePage.route) { HomePage() }
        composable(Screen.DiaryPage.route) { DiaryPage() }
        composable(Screen.ProfilePage.route) { ProfilePage() }

        composable(Screen.Onboarding.route) {  }
        composable(Screen.RecipePage.route) {  }
        composable(Screen.HistoryPage.route) {  }
        composable(Screen.PersonalDataPage.route) {  }
        composable(Screen.RecipeOverview.route) {  }
        composable(Screen.SearchPage.route) {  }
        composable(Screen.DishItemOverview.route) {  }
        composable(Screen.WeightHistoryPage.route) {  }

    }
}