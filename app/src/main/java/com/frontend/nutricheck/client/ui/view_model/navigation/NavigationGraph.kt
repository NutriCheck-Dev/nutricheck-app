package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.frontend.nutricheck.client.ui.view.app_views.DiaryPage
import com.frontend.nutricheck.client.ui.view.app_views.HomePage
import com.frontend.nutricheck.client.ui.view.app_views.ProfilePage

@Composable
fun NavigationGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val actions = remember(navController) { NavigationActions(navController)}

    NavHost(
        navController = navController,
        startDestination = Screen.HomePage.route,
        modifier = modifier
    ) {
        composable(Screen.HomePage.route) { HomePage(actions = actions) }
        composable(Screen.DiaryPage.route) { DiaryPage(actions = actions) }
        composable(Screen.ProfilePage.route) { ProfilePage(actions = actions) }
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