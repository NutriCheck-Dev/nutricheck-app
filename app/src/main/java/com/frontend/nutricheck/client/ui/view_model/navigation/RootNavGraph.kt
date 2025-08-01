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

    data object DishItemOverview : Screen("dish_item_overview/{dishId}") {
        fun createRoute(dishId: String) = "dish_item_overview/$dishId"
    }

    data object Add : Screen("add/{origin}") {
        fun createRoute(origin: AddDialogOrigin) = "add/${origin.name}"
    }
}
    @Composable
fun RootNavGraph(mainNavController: NavHostController, startDestination: String) {

    NavHost(
        navController = mainNavController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) { OnboardingNavGraph(mainNavController) }

        composable(Screen.HomePage.route) { HomeNavGraph() }
        composable(Screen.DiaryPage.route) { DiaryNavGraph(mainNavController) }
        composable(Screen.ProfilePage.route) { ProfilePageNavGraph() }
        composable(Screen.Add.route) { backStackEntry ->
            val originArg = backStackEntry.arguments?.getString("origin")
            val effectiveOriginName: String = when (originArg) {
                null, "{origin}" -> AddDialogOrigin.BOTTOM_NAV_BAR.name
                else -> originArg
            }
            val origin = AddDialogOrigin.valueOf(effectiveOriginName)
            AddNavGraph(
                mainNavController = mainNavController,
                origin = origin
            )
        }


        composable(Screen.DishItemOverview.route) {  }
        }
}