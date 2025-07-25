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
import com.frontend.nutricheck.client.ui.view.dialogs.AddWeightDialog
import com.frontend.nutricheck.client.ui.view.dialogs.ChooseLanguageDialog
import com.frontend.nutricheck.client.ui.view_model.profile.ProfileEvent
import com.frontend.nutricheck.client.ui.view_model.profile.ProfileOverviewViewModel

sealed class ProfileScreens(val route: String) {
    object ProfilePage : ProfileScreens("profile_page_route")
    object WeightHistoryPage : ProfileScreens("weight_history_page_route")
    object PersonalDataPage : ProfileScreens("personal_data_page_route")
    object SelectLanguageDialog : ProfileScreens("select_language_dialog_route")
    object AddWeightDialog : ProfileScreens("add_weight_dialog_route")

}

@Composable
fun ProfilePageNavGraph() {
    val profileOverviewViewModel : ProfileOverviewViewModel = hiltViewModel()
    val profileNavController = rememberNavController()
    val state by profileOverviewViewModel.data.collectAsState()
    //val weightState by profileOverviewViewModel.weightData.collectAsState()


    LaunchedEffect(key1 = Unit) {
        profileOverviewViewModel.events.collect { event ->
            when (event) {
                is ProfileEvent.DisplayWeightHistory -> {
                    profileNavController.navigate(ProfileScreens.WeightHistoryPage.route)
                }
                is ProfileEvent.DisplayPersonalData -> {
                    profileNavController.navigate(ProfileScreens.PersonalDataPage.route)
                }
                is ProfileEvent.SelectLanguage -> {
                    profileNavController.navigate(ProfileScreens.SelectLanguageDialog.route)
                }
                is ProfileEvent.DisplayProfileOverview -> {
                    profileNavController.navigate(ProfileScreens.ProfilePage.route)
                }
                is  ProfileEvent.AddNewWeight -> {
                    profileNavController.navigate(ProfileScreens.AddWeightDialog.route)
                }
                else -> { /* No action needed for other events */}
            }
        }
    }


    NavHost(
        navController = profileNavController,
        startDestination = ProfileScreens.ProfilePage.route
    ) {
        composable(ProfileScreens.ProfilePage.route) {
            ProfilePage(
                state = state,
                onEvent = profileOverviewViewModel::onEvent)
        }
        composable(ProfileScreens.WeightHistoryPage.route) {
            WeightHistoryPage(
                weightState = emptyList(), //TODO("change parameter to state.weightData")
                onEvent = profileOverviewViewModel::onEvent,
                onBack = { profileNavController.popBackStack() })
        }
        composable(ProfileScreens.PersonalDataPage.route) {
            PersonalDataPage(
                state = state,
                onEvent = profileOverviewViewModel::onEvent,
                onBack = { profileNavController.popBackStack() })
        }
        dialog(ProfileScreens.SelectLanguageDialog.route) {
            ChooseLanguageDialog(
                currentLanguage = state.userData.language,
                onEvent = profileOverviewViewModel::onEvent,
                onDismissRequest = { profileNavController.popBackStack() })
        }
        dialog(ProfileScreens.AddWeightDialog.route) {
            AddWeightDialog(
                state = state,
                onEvent = profileOverviewViewModel::onEvent,
                onDismissRequest = { profileNavController.popBackStack() }
            )
        }
    }
}
