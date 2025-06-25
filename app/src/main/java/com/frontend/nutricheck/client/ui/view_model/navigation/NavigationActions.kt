package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.navigation.NavHostController

sealed class Screen(val route: String) {
    data object HomePage : Screen("home")
    data object DiaryPage : Screen("diary")
    data object ProfilePage : Screen("profile")
    data object RecipePage : Screen("diary/recipe")
    data object HistoryPage : Screen("diary/history")
    data object Onboarding : Screen("onboarding")
    data object PersonalDataPage : Screen("profile/personal_data")
    data object RecipeOverview : Screen("recipe_overview")
    data object SearchPage : Screen("search")
    data object WeightHistoryPage : Screen("profile/weight_history")
    data object DishItemOverview : Screen("dish_item_overview/{dishId}") {
        fun createRoute(dishId: String) = "dish_item_overview/$dishId"
    }

}

class NavigationActions(private val navController: NavHostController) {
    fun toHome() = navController.navigate(Screen.HomePage.route)
    fun toDiary() = navController.navigate(Screen.DiaryPage.route)
    fun toProfile() = navController.navigate(Screen.ProfilePage.route)
    fun toRecipe() = navController.navigate(Screen.RecipePage.route)
    fun toHistory() = navController.navigate(Screen.HistoryPage.route)
    fun toOnboarding() = navController.navigate(Screen.Onboarding.route)
    fun toPersonalData() = navController.navigate(Screen.PersonalDataPage.route)
    fun toRecipeOverview() = navController.navigate(Screen.RecipeOverview.route)
    fun toSearch() = navController.navigate(Screen.SearchPage.route)
    fun toWeightHistory() = navController.navigate(Screen.WeightHistoryPage.route)
    fun toDishItemOverview(dishId: String) = navController.navigate(Screen.DishItemOverview.createRoute(dishId))

    fun goBack() = navController.popBackStack()
}