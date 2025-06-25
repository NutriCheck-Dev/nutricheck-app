package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.navigation.NavHostController

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Diary : Screen("diary")
    data object Profile : Screen("profile")
    data object Recipe : Screen("recipe")
    data object History : Screen("history")

}

class NavigationActions(private val navController: NavHostController) {
    fun toHome() = navController.navigate(Screen.Home.route)
    fun toDiary() = navController.navigate(Screen.Diary.route)
    fun toProfile() = navController.navigate(Screen.Profile.route)

    fun goBack() = navController.popBackStack()
}