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
import com.frontend.nutricheck.client.ui.view.dialogs.DeleteWeightDialog
import com.frontend.nutricheck.client.ui.view_model.ProfileEvent
import com.frontend.nutricheck.client.ui.view_model.ProfileViewModel

sealed class ProfileScreens(val route: String) {
    object ProfilePage : ProfileScreens("profile_page_route")
    object WeightHistoryPage : ProfileScreens("weight_history_page_route")
    object PersonalDataPage : ProfileScreens("personal_data_page_route")
    object AddWeightDialog : ProfileScreens("add_weight_dialog_route")
    object DeleteWeightDialog : ProfileScreens("delete_weight_dialog_route")

}

@Composable
fun ProfilePageNavGraph() {
    val profileViewModel : ProfileViewModel = hiltViewModel()
    val profileNavController = rememberNavController()
    val state by profileViewModel.data.collectAsState()
    val uiState by profileViewModel.uiState.collectAsState()
    val userDataDraft by profileViewModel.dataDraft.collectAsState()
    val weightState by profileViewModel.weightData.collectAsState()
    val selectedWeight by profileViewModel.selectedWeight.collectAsState()
    val context = LocalContext.current


    LaunchedEffect(key1 = Unit) {
        profileViewModel.events.collect { event ->
            when (event) {
                is ProfileEvent.DisplayWeightHistory -> {
                    profileNavController.navigate(ProfileScreens.WeightHistoryPage.route) {
                    launchSingleTop = true
                    }
                }
                is ProfileEvent.NavigateToPersonalData -> {
                    profileNavController.navigate(ProfileScreens.PersonalDataPage.route) {
                        launchSingleTop = true
                    }
                }
                is ProfileEvent.NavigateToProfileOverview -> {
                    profileNavController.navigate(ProfileScreens.ProfilePage.route) {
                        launchSingleTop = true
                    }
                }
                is  ProfileEvent.NavigateToAddNewWeight -> {
                    profileNavController.navigate(ProfileScreens.AddWeightDialog.route) {
                        launchSingleTop = true
                    }
                }
                is ProfileEvent.NavigateToDeleteWeightDialog -> {
                    profileNavController.navigate(ProfileScreens.DeleteWeightDialog.route) {
                        launchSingleTop = true
                    }
                }
                is ProfileEvent.RestartApp -> {
                    (context as? Activity)?.recreate()
                }
                is ProfileEvent.NavigateBack -> {
                    profileNavController.popBackStack()
                }
                else -> { /* No action needed for other events */ }
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
                errorState = uiState,
                onEvent = profileViewModel::onEvent,
                onBack = { profileNavController.popBackStack() })
        }
        dialog(ProfileScreens.AddWeightDialog.route) {
            AddWeightDialog(
                errorState = uiState,
                onEvent = profileViewModel::onEvent,
                onDismissRequest = { profileNavController.popBackStack() }
            )
        }
        dialog(ProfileScreens.DeleteWeightDialog.route) {
            DeleteWeightDialog(
                onDismissRequest = { profileNavController.popBackStack() },
                onEvent = profileViewModel::onEvent,
                selectedWeight = selectedWeight
            )
        }
    }
}
