package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.frontend.nutricheck.client.ui.view.app_views.PersonalDataPage
import com.frontend.nutricheck.client.ui.view.app_views.ProfilePage
import com.frontend.nutricheck.client.ui.view.app_views.WeightHistoryPage
import com.frontend.nutricheck.client.ui.view.dialogs.ChooseLanguageDialog
import com.frontend.nutricheck.client.ui.view_model.profile.ProfileOverviewEvent
import com.frontend.nutricheck.client.ui.view_model.profile.ProfileOverviewViewModel

sealed class ProfileScreens(val route: String) {
    object ProfilePage : ProfileScreens("profile_page_route")
    object WeightHistoryPage : ProfileScreens("weight_history_page_route")
    object PersonalDataPage : ProfileScreens("personal_data_page_route")
    object SelectLanguageDialog : ProfileScreens("select_language_dialog_route")

}

@Composable
fun ProfilePageNavGraph() {
    val profileOverviewViewModel : ProfileOverviewViewModel = hiltViewModel()
    val profileNavController = rememberNavController()

    LaunchedEffect(key1 = Unit) {
        profileOverviewViewModel.events.collect { event ->
            when (event) {
                is ProfileOverviewEvent.DisplayWeightHistory -> {
                    profileNavController.navigate(ProfileScreens.WeightHistoryPage.route)
                }
                is ProfileOverviewEvent.DisplayPersonalData -> {
                    profileNavController.navigate(ProfileScreens.PersonalDataPage.route)
                }
                is ProfileOverviewEvent.SelectLanguage -> {
                    profileNavController.navigate(ProfileScreens.SelectLanguageDialog.route)
                }
                else -> { /* No action needed for other events */}
            }
        }
    }
    NavHost(
        navController = profileNavController,
        startDestination = ProfileScreens.ProfilePage.route
    ) {
        composable(ProfileScreens.ProfilePage.route) { ProfilePage(profileOverviewViewModel) }
        composable(ProfileScreens.WeightHistoryPage.route) {
            WeightHistoryPage(profileOverviewViewModel) }
        composable(ProfileScreens.PersonalDataPage.route) {
            PersonalDataPage(profileOverviewViewModel) }

        dialog(ProfileScreens.SelectLanguageDialog.route) {
            ChooseLanguageDialog()
        }
    }
}
