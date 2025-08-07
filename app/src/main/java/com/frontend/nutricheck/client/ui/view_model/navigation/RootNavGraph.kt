package com.frontend.nutricheck.client.ui.view_model.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.FoodProductOverview
import com.frontend.nutricheck.client.ui.view.app_views.foodcomponent.RecipeOverview
import com.frontend.nutricheck.client.ui.view_model.food.FoodProductOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.ReportRecipeViewModel
import com.frontend.nutricheck.client.ui.view_model.search_food_component.FoodSearchViewModel
import java.util.Date

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object HomePage : Screen("home")
    data object DiaryPage : Screen("diary")
    data object ProfilePage : Screen("profile")

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
        composable(Screen.Add.route) { backStackEntry ->
            val originStr = backStackEntry.arguments?.getString("origin")
            val origin = runCatching { AddDialogOrigin.valueOf(originStr ?: "BOTTOM_NAV_BAR") }.getOrDefault(AddDialogOrigin.BOTTOM_NAV_BAR)
            val date = backStackEntry.arguments?.getString("date")?.toLongOrNull() ?: Date().time
            val dayTimeStr = backStackEntry.arguments?.getString("dayTime")
            val dayTime = dayTimeStr?.let { runCatching { DayTime.valueOf(it) }.getOrNull() }

            Log.v("RootNavGraph", "→ AddNavGraph mit $origin / $date / $dayTime")

            AddNavGraph(
                mainNavController = mainNavController,
                origin = origin,
                date = date,
                dayTime = dayTime
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