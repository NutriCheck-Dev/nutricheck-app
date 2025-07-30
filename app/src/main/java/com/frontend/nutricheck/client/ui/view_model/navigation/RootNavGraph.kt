package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object HomePage : Screen("home")
    data object DiaryPage : Screen("diary")
    data object ProfilePage : Screen("profile")
    data object Add : Screen("add")


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

        composable(Screen.HomePage.route) { HomeNavGraph() }
        composable(Screen.DiaryPage.route) { DiaryNavGraph(mainNavController) }
        composable(Screen.ProfilePage.route) { ProfilePageNavGraph() }
        dialog(Screen.Add.route) {
            AddNavGraph(
                mainNavController = mainNavController,
                origin = AddDialogOrigin.BOTTOM_NAV_BAR
            )
        }


        composable(Screen.DishItemOverview.route) {  }
        }
}