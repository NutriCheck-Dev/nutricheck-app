package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.navigation.NavHostController

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Diary : Screen("diary")
    object Profile : Screen("profile")
}

class NavigationActions(private val navController: NavHostController) {
    fun toHome() = navController.navigate(Screen.Home.route)
    fun toDiary() = navController.navigate(Screen.Diary.route)
    fun toProfile() = navController.navigate(Screen.Profile.route)

    fun goBack() = navController.popBackStack()
}