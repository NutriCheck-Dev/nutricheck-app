package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.frontend.nutricheck.client.ui.view.dialogs.AddDialog
import com.frontend.nutricheck.client.ui.view_model.snackbar.UiEventViewModel

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object HomePage : Screen("home")
    data object DiaryPage : Screen("diary?destination={destination}") {
        fun createRoute(destination: DiaryGraphDestination?): String =
            if (destination == null) "diary"
            else "diary?destination=${destination.name}"

    }
    data object ProfilePage : Screen("profile")
    data object AddButton : Screen("add_button")

    data object Add : Screen("add/{origin}") {
        fun createRoute(origin: AddDialogOrigin): String {
            return "add/${origin.name}"
        }
    }
}
@Composable
fun RootNavGraph(mainNavController: NavHostController, startDestination: String) {
    val uiEventVm: UiEventViewModel = hiltViewModel()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        uiEventVm.snackbarManager.messages.collect { msg ->
            snackbarHostState.showSnackbar(message = msg)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { _ ->
    NavHost(
            navController = mainNavController,
            startDestination = startDestination
        ) {
            composable(Screen.Onboarding.route) { OnboardingNavGraph(mainNavController) }
            composable(Screen.HomePage.route) { HomeNavGraph() }
            composable(
                route = Screen.DiaryPage.route,
                arguments = listOf(
                    navArgument("destination") {
                        type = NavType.StringType
                        nullable = true
                    }
                )
            ) { backStack ->
                val destinationName = backStack.arguments?.getString("destination")
                val destination = destinationName
                    ?.let { runCatching { DiaryGraphDestination.valueOf(it) }.getOrNull() }
                    ?: DiaryGraphDestination.HISTORY_RELATED
                DiaryNavGraph(destination = destination)
            }
            composable(Screen.ProfilePage.route) { ProfilePageNavGraph() }

            dialog(Screen.AddButton.route) {
                AddDialog(
                    onAddMealClick = {
                        mainNavController.navigate(
                            Screen.Add.createRoute(
                                origin = AddDialogOrigin.BOTTOM_NAV_BAR_ADD_MEAL,
                            )
                        )
                    },
                    onAddRecipeClick = {
                        mainNavController.navigate(
                            Screen.Add.createRoute(
                                origin = AddDialogOrigin.BOTTOM_NAV_BAR_ADD_RECIPE,
                            )
                        )
                    },
                    onScanFoodClick = {
                        mainNavController.navigate(
                            Screen.Add.createRoute(
                                origin = AddDialogOrigin.BOTTOM_NAV_BAR_ADD_AI_MEAL,
                            )
                        )
                    },
                    onDismissRequest = { mainNavController.popBackStack() }
                )
            }

            composable(Screen.Add.route) { backStackEntry ->
                val originArg = backStackEntry.arguments?.getString("origin")

                val effectiveOriginName = when (originArg) {
                    null, "{origin}" -> AddDialogOrigin.BOTTOM_NAV_BAR_ADD_MEAL.name
                    else -> originArg
                }

                AddNavGraph(
                    mainNavController = mainNavController,
                    origin = AddDialogOrigin.valueOf(effectiveOriginName),
                )
            }
        }
    }
}
