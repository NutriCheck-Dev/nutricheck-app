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
fun NavigationGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val actions = remember(navController) { NavigationActions(navController)}

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) { HomePage(actions = actions) }
        composable(Screen.Diary.route) { DiaryPage(actions = actions) }
        composable(Screen.Profile.route) { ProfilePage(actions = actions) }
    }
}