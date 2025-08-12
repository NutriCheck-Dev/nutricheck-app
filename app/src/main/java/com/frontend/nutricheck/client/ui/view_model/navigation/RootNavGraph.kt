package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.FoodProductOverview
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.RecipeOverview
import com.frontend.nutricheck.client.ui.view.dialogs.AddDialog
import com.frontend.nutricheck.client.ui.view_model.food.FoodProductOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.ReportRecipeViewModel
import com.frontend.nutricheck.client.ui.view_model.FoodSearchViewModel
import java.util.Date

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object HomePage : Screen("home")
    data object DiaryPage : Screen("diary")
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

    NavHost(
        navController = mainNavController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) { OnboardingNavGraph(mainNavController) }

        composable(Screen.HomePage.route) { HomeNavGraph() }
        composable(Screen.DiaryPage.route) { DiaryNavGraph(mainNavController) }
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
                onDismissRequest = {
                    mainNavController.popBackStack()
                }
            )
        }


        composable(Screen.Add.route) { backStackEntry ->
            val originArg = backStackEntry.arguments?.getString("origin")
            val dateArg = backStackEntry.arguments?.getString("date")
            val dayTimeArg = backStackEntry.arguments?.getString("dayTime")
            val effectiveOriginName: String = when (originArg) {
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
            val origin = AddDialogOrigin.valueOf(effectiveOriginName)
            AddNavGraph(
                mainNavController = mainNavController,
                origin = origin,
                date = effectiveDateLong,
                dayTime = DayTime.valueOf(effectiveDayTimeName)
            )
        }

        composable(
            route = "food_details?mealId={mealId}&foodProductId={foodProductId}",
            arguments = listOf(
                navArgument("mealId") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("foodProductId") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val foodProductOverviewViewModel: FoodProductOverviewViewModel = hiltViewModel()
            FoodProductOverview(
                foodProductOverviewViewModel = foodProductOverviewViewModel,
                onBack = { mainNavController.navigate(Screen.DiaryPage.route) }
            )
        }
        composable(
            route = "recipe_details?recipeId={recipeId}&mealId={mealId}",
            arguments = listOf(
                navArgument("recipeId") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("mealId") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) {
            val recipeOverviewViewModel: RecipeOverviewViewModel = hiltViewModel()
            val reportRecipeViewModel: ReportRecipeViewModel = hiltViewModel()
            val searchViewModel: FoodSearchViewModel = hiltViewModel()
            RecipeOverview(
                recipeOverviewViewModel = recipeOverviewViewModel,
                reportRecipeViewModel = reportRecipeViewModel,
                searchViewModel = searchViewModel,
                onBack = { mainNavController.navigate(Screen.DiaryPage.route) }
            )
        }
    }
}