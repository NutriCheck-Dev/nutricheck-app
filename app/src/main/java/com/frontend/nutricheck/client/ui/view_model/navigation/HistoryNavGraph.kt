package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.frontend.nutricheck.client.ui.view.app_views.HistoryPage

sealed class HistoryPageScreens(val route: String) {
    object HistoryPage : HistoryPageScreens("history_page")
}

@Composable
fun HistoryPageNavGraph(
    mainNavGraph: NavHostController
) {
    val historyPageNavController = rememberNavController()

    NavHost(
        navController = historyPageNavController,
        startDestination = HistoryPageScreens.HistoryPage.route
    ) {
        composable(HistoryPageScreens.HistoryPage.route) {
            HistoryPage(
                historyViewModel = hiltViewModel(),
                mainNavController = mainNavGraph,
            )
        }
    }
}