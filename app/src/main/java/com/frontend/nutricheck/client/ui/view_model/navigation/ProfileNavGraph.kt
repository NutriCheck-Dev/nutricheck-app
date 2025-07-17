package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.getValue
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
    val state by profileOverviewViewModel.data.collectAsState()


    NavHost(
        navController = profileNavController,
        startDestination = ProfileScreens.ProfilePage.route
    ) {
        composable(ProfileScreens.ProfilePage.route) {
            ProfilePage(
                state = state,
                onEvent = profileOverviewViewModel::onEvent,
                profileNavController = profileNavController,
                profileOverviewViewModel = profileOverviewViewModel) }

        composable(ProfileScreens.WeightHistoryPage.route) {
            WeightHistoryPage(profileOverviewViewModel) }
        composable(ProfileScreens.PersonalDataPage.route) {
            PersonalDataPage(
                state = state,
                onEvent = profileOverviewViewModel::onEvent) }

        dialog(ProfileScreens.SelectLanguageDialog.route) {
            ChooseLanguageDialog(
                profileOverviewViewModel = profileOverviewViewModel,
                currentLanguage = state.selectedLanguage,
                onDismissRequest = {
                    profileNavController.popBackStack()
                })
        }
    }
}
