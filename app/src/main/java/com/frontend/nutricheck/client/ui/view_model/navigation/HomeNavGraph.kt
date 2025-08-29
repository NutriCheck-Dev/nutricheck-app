package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.frontend.nutricheck.client.ui.view.app_views.HomePage
import com.frontend.nutricheck.client.ui.view_model.dashboard.CalorieHistoryViewModel
import com.frontend.nutricheck.client.ui.view_model.dashboard.DailyCalorieViewModel
import com.frontend.nutricheck.client.ui.view_model.dashboard.DailyMacrosViewModel
import com.frontend.nutricheck.client.ui.view_model.dashboard.WeightHistoryViewModel


sealed class HomeScreens(val route: String) {
    data object HomePage : HomeScreens("home_page_route")

}

@Composable
fun HomeNavGraph() {
    val calorieHistoryViewModel : CalorieHistoryViewModel = hiltViewModel()
    val dailyCalorieViewModel : DailyCalorieViewModel = hiltViewModel()
    val dailyMacrosViewModel : DailyMacrosViewModel = hiltViewModel()
    val weightHistoryViewModel : WeightHistoryViewModel = hiltViewModel()

    val dashboardNavController = rememberNavController()


    NavHost(
        navController = dashboardNavController,
        startDestination = HomeScreens.HomePage.route,
    ) {
        composable(HomeScreens.HomePage.route) {
            HomePage(
                calorieHistoryViewModel = calorieHistoryViewModel,
                dailyCalorieViewModel = dailyCalorieViewModel,
                dailyMacrosViewModel = dailyMacrosViewModel,
                weightHistoryViewModel = weightHistoryViewModel
            )
        }
    }
}
