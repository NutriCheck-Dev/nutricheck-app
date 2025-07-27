package com.frontend.nutricheck.client.ui.view_model.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.frontend.nutricheck.client.ui.view.app_views.PersonalDataPage
import com.frontend.nutricheck.client.ui.view.app_views.ProfilePage
import com.frontend.nutricheck.client.ui.view.app_views.WeightHistoryPage
import com.frontend.nutricheck.client.ui.view.dialogs.AddWeightDialog
import com.frontend.nutricheck.client.ui.view.dialogs.ChooseLanguageDialog
import com.frontend.nutricheck.client.ui.view_model.profile.ProfileEvent
import com.frontend.nutricheck.client.ui.view_model.profile.ProfileViewModel

sealed class ProfileScreens(val route: String) {
    object ProfilePage : ProfileScreens("profile_page_route")
    object WeightHistoryPage : ProfileScreens("weight_history_page_route")
    object PersonalDataPage : ProfileScreens("personal_data_page_route")
    object ChooseLanguageDialog : ProfileScreens("select_language_dialog_route")
    object AddWeightDialog : ProfileScreens("add_weight_dialog_route")

}

@Composable
fun ProfilePageNavGraph() {
    val profileViewModel : ProfileViewModel = hiltViewModel()
    val profileNavController = rememberNavController()
    val state by profileViewModel.data.collectAsState()
    val errorMessage by profileViewModel.errorMessage.collectAsState()
    val userDataDraft by profileViewModel.dataDraft.collectAsState()
    val weightState by profileViewModel.weightData.collectAsState()
    val language by profileViewModel.currentLanguage.collectAsState()
    val context = LocalContext.current


    LaunchedEffect(key1 = Unit) {
        profileViewModel.events.collect { event ->
            when (event) {
                is ProfileEvent.DisplayWeightHistory -> {
                    profileNavController.navigate(ProfileScreens.WeightHistoryPage.route)
                }
                is ProfileEvent.DisplayPersonalData -> {
                    profileNavController.navigate(ProfileScreens.PersonalDataPage.route)
                }
                is ProfileEvent.SelectLanguage -> {
                    profileNavController.navigate(ProfileScreens.ChooseLanguageDialog.route)
                }
                is ProfileEvent.DisplayProfileOverview -> {
                    profileNavController.navigate(ProfileScreens.ProfilePage.route)
                }
                is  ProfileEvent.AddNewWeight -> {
                    profileNavController.navigate(ProfileScreens.AddWeightDialog.route)
                }
                is ProfileEvent.RestartApp -> {
                    (context as? Activity)?.recreate()
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
                onEvent = profileViewModel::onEvent)
        }
        composable(ProfileScreens.WeightHistoryPage.route) {
            WeightHistoryPage(
                weightState = weightState,
                onEvent = profileViewModel::onEvent,
                onBack = { profileNavController.popBackStack() })
        }
        composable(ProfileScreens.PersonalDataPage.route) {
            PersonalDataPage(
                state = userDataDraft,
                errorMessage = errorMessage,
                onEvent = profileViewModel::onEvent,
                onBack = { profileNavController.popBackStack() })
        }
        dialog(ProfileScreens.ChooseLanguageDialog.route) {
            ChooseLanguageDialog(
                currentLanguage = language ,
                onEvent = profileViewModel::onEvent,
                onDismissRequest = { profileNavController.popBackStack() })
        }
        dialog(ProfileScreens.AddWeightDialog.route) {
            AddWeightDialog(
                errorMessage = errorMessage,
                onEvent = profileViewModel::onEvent,
                onDismissRequest = { profileNavController.popBackStack() }
            )
        }
    }
}
