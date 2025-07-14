package com.frontend.nutricheck.client.ui.view_model.navigation

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