package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.material.Scaffold
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
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.ui.view.dialogs.AddDialog
import com.frontend.nutricheck.client.ui.view_model.snackbar.AppSnackbarHost
import com.frontend.nutricheck.client.ui.view_model.snackbar.UiEventViewModel
import java.util.Date

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

    data object Add : Screen("add/{origin}/{date}/{dayTime}") {
        fun createRoute(origin: AddDialogOrigin, date: Date, dayTime: DayTime): String {
            return "add/${origin.name}/${date.time}/${dayTime.name}"
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
        snackbarHost = { AppSnackbarHost(snackbarHostState = snackbarHostState) }
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
                                date = Date(),
                                dayTime = DayTime.dateToDayTime(Date())
                            )
                        )
                    },
                    onAddRecipeClick = {
                        mainNavController.navigate(
                            Screen.Add.createRoute(
                                origin = AddDialogOrigin.BOTTOM_NAV_BAR_ADD_RECIPE,
                                date = Date(),
                                dayTime = DayTime.dateToDayTime(Date())
                            )
                        )
                    },
                    onScanFoodClick = {
                        mainNavController.navigate(
                            Screen.Add.createRoute(
                                origin = AddDialogOrigin.BOTTOM_NAV_BAR_ADD_AI_MEAL,
                                date = Date(),
                                dayTime = DayTime.dateToDayTime(Date())
                            )
                        )
                    },
                    onDismissRequest = { mainNavController.popBackStack() }
                )
            }

            composable(Screen.Add.route) { backStackEntry ->
                val originArg = backStackEntry.arguments?.getString("origin")
                val dateArg = backStackEntry.arguments?.getString("date")
                val dayTimeArg = backStackEntry.arguments?.getString("dayTime")

                val effectiveOriginName = when (originArg) {
                    null, "{origin}" -> AddDialogOrigin.BOTTOM_NAV_BAR_ADD_MEAL.name
                    else -> originArg
                }
                val effectiveDateLong = when (dateArg) {
                    null, "{date}" -> Date().time
                    else -> dateArg.toLongOrNull() ?: Date().time
                }
                val effectiveDayTimeName = when (dayTimeArg) {
                    null, "{dayTime}" -> DayTime.BREAKFAST.name
                    else -> dayTimeArg
                }

                AddNavGraph(
                    mainNavController = mainNavController,
                    origin = AddDialogOrigin.valueOf(effectiveOriginName),
                    date = effectiveDateLong,
                    dayTime = DayTime.valueOf(effectiveDayTimeName)
                )
            }
        }
    }
}
