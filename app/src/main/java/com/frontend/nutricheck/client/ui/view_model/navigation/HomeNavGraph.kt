package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.frontend.nutricheck.client.ui.view.app_views.HomePage
import com.frontend.nutricheck.client.ui.view_model.dashboard.DashboardViewModel


sealed class HomeScreens(val route: String) {
    object HomePage : HomeScreens("home_page_route")
    //evlt WeighthistoryPage?
    //evtl HistoryPage?

}

@Composable
fun HomeNavGraph() {
    val dashboardViewModel : DashboardViewModel = hiltViewModel()
    val dashboardNavController = rememberNavController()
    val state = dashboardViewModel.data

    NavHost(
        navController = dashboardNavController,
        startDestination = HomeScreens.HomePage.route,
    ) {
        composable(HomeScreens.HomePage.route) {
            HomePage(state = state)
        }
    }
}
