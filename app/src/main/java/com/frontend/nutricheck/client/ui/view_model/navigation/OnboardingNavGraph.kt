package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.frontend.nutricheck.client.ui.view.app_views.onboarding.OnboardingBirthdate
import com.frontend.nutricheck.client.ui.view.app_views.onboarding.OnboardingGender
import com.frontend.nutricheck.client.ui.view.app_views.onboarding.OnboardingGoal
import com.frontend.nutricheck.client.ui.view.app_views.onboarding.OnboardingHeight
import com.frontend.nutricheck.client.ui.view.app_views.onboarding.OnboardingName
import com.frontend.nutricheck.client.ui.view.app_views.onboarding.OnboardingSport
import com.frontend.nutricheck.client.ui.view.app_views.onboarding.OnboardingTargetWeight
import com.frontend.nutricheck.client.ui.view.app_views.onboarding.OnboardingWeight
import com.frontend.nutricheck.client.ui.view.app_views.onboarding.OnboardingWelcome
import com.frontend.nutricheck.client.ui.view_model.OnboardingEvent
import com.frontend.nutricheck.client.ui.view_model.OnboardingViewModel


sealed class OnboardingScreen(val route: String) {
    data object Welcome : OnboardingScreen("welcome_route")
    data object Name : OnboardingScreen("name_route")
    data object Birthdate : OnboardingScreen("birthdate_route")
    data object Gender : OnboardingScreen("gender_route")
    data object Height : OnboardingScreen("height_route")
    data object Weight : OnboardingScreen("weight_route")
    data object SportFrequency : OnboardingScreen("sport_frequency_route")
    data object WeightGoal : OnboardingScreen("weight_goal_route")
    data object TargetWeight : OnboardingScreen("target_weight_route")
}

@Composable
fun OnboardingNavGraph(
    mainNavController : NavHostController
) {

    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val onboardingNavController = rememberNavController()
    val state by onboardingViewModel.data.collectAsState()
    val uiState by onboardingViewModel.uiState.collectAsState()
    LaunchedEffect(key1 = Unit) {
        onboardingViewModel.events.collect { event ->
            when (event) {
                is OnboardingEvent.NavigateToName ->
                    onboardingNavController.navigate(OnboardingScreen.Name.route)
                is OnboardingEvent.NavigateToBirthdate ->
                    onboardingNavController.navigate(OnboardingScreen.Birthdate.route)
                is OnboardingEvent.NavigateToGender ->
                    onboardingNavController.navigate(OnboardingScreen.Gender.route)
                is OnboardingEvent.NavigateToHeight ->
                    onboardingNavController.navigate(OnboardingScreen.Height.route)
                is OnboardingEvent.NavigateToWeight ->
                    onboardingNavController.navigate(OnboardingScreen.Weight.route)
                is OnboardingEvent.NavigateToSportFrequency ->
                    onboardingNavController.navigate(OnboardingScreen.SportFrequency.route)
                is OnboardingEvent.NavigateToWeightGoal ->
                    onboardingNavController.navigate(OnboardingScreen.WeightGoal.route)
                is OnboardingEvent.NavigateToTargetWeight ->
                    onboardingNavController.navigate(OnboardingScreen.TargetWeight.route)
                is OnboardingEvent.NavigateToDashboard -> {
                    mainNavController.navigate(Screen.HomePage.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
                else -> { /* No action needed for other events (non navigation events) */}
            }
        }
    }

    NavHost(
        navController = onboardingNavController,
        startDestination = OnboardingScreen.Welcome.route
    ) {
        composable(OnboardingScreen.Welcome.route) {
            OnboardingWelcome(onEvent = onboardingViewModel::onEvent)
        }
        composable(OnboardingScreen.Name.route) {
            OnboardingName(
                state = state,
                onEvent = onboardingViewModel::onEvent,
                errorState = uiState
                )
        }
        composable(OnboardingScreen.Birthdate.route) {
            OnboardingBirthdate(state = state,
                onEvent = onboardingViewModel::onEvent,
                errorState = uiState)
        }
        composable(OnboardingScreen.Gender.route) {
            OnboardingGender(state = state,
                onEvent = onboardingViewModel::onEvent,
                errorState = uiState)
        }
        composable(OnboardingScreen.Height.route) {
            OnboardingHeight(state = state,
                onEvent = onboardingViewModel::onEvent,
                errorState = uiState)
        }
        composable(OnboardingScreen.Weight.route) {
            OnboardingWeight(state = state,
                onEvent = onboardingViewModel::onEvent,
                errorState = uiState)
        }
        composable(OnboardingScreen.SportFrequency.route) {
            OnboardingSport(state = state,
                onEvent = onboardingViewModel::onEvent,
                errorState = uiState)
        }
        composable(OnboardingScreen.WeightGoal.route) {
            OnboardingGoal(state = state,
                onEvent = onboardingViewModel::onEvent,
                errorState = uiState)
        }
        composable(OnboardingScreen.TargetWeight.route) {
            OnboardingTargetWeight(state = state,
                onEvent = onboardingViewModel::onEvent,
                errorState = uiState)
        }
    }
}
