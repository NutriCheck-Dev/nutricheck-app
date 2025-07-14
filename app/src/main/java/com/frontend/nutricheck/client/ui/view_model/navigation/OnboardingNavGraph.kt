package com.frontend.nutricheck.client.ui.view_model.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.frontend.nutricheck.client.ui.view.app_views.OnboardingBirthdate
import com.frontend.nutricheck.client.ui.view.app_views.OnboardingGender
import com.frontend.nutricheck.client.ui.view.app_views.OnboardingGoal
import com.frontend.nutricheck.client.ui.view.app_views.OnboardingHeight
import com.frontend.nutricheck.client.ui.view.app_views.OnboardingName
import com.frontend.nutricheck.client.ui.view.app_views.OnboardingSport
import com.frontend.nutricheck.client.ui.view.app_views.OnboardingTargetWeight
import com.frontend.nutricheck.client.ui.view.app_views.OnboardingWeight
import com.frontend.nutricheck.client.ui.view.app_views.OnboardingWelcome
import com.frontend.nutricheck.client.ui.view_model.onboarding.OnboardingEvent
import com.frontend.nutricheck.client.ui.view_model.onboarding.OnboardingViewModel


sealed class OnboardingScreen(val route: String) {
    object Welcome : OnboardingScreen("welcome_route")
    object Name : OnboardingScreen("name_route")
    object Birthdate : OnboardingScreen("birthdate_route")
    object Gender : OnboardingScreen("gender_route")
    object Height : OnboardingScreen("height_route")
    object Weight : OnboardingScreen("weight_route")
    object SportFrequency : OnboardingScreen("sport_frequency_route")
    object WeightGoal : OnboardingScreen("weight_goal_route")
    object TargetWeight : OnboardingScreen("target_weight_route")
}

@Composable
fun OnboardingNavGraph(
    mainNavController : NavHostController,
    onboardingViewModel: OnboardingViewModel = hiltViewModel()
) {
    val onboardingNavController = rememberNavController()

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
                else -> { /* No action needed for other events */}
            }
        }
    }
    NavHost(
        navController = onboardingNavController,
        startDestination = OnboardingScreen.Welcome.route
    ) {
        composable(OnboardingScreen.Welcome.route) { OnboardingWelcome(onboardingViewModel) }
        composable(OnboardingScreen.Name.route) { OnboardingName(onboardingViewModel) }
        composable(OnboardingScreen.Birthdate.route) { OnboardingBirthdate(onboardingViewModel) }
        composable(OnboardingScreen.Gender.route) { OnboardingGender(onboardingViewModel) }
        composable(OnboardingScreen.Height.route) { OnboardingHeight(onboardingViewModel) }
        composable(OnboardingScreen.Weight.route) { OnboardingWeight(onboardingViewModel) }
        composable(OnboardingScreen.SportFrequency.route) { OnboardingSport(onboardingViewModel) }
        composable(OnboardingScreen.WeightGoal.route) { OnboardingGoal(onboardingViewModel) }
        composable(OnboardingScreen.TargetWeight.route) {
            OnboardingTargetWeight(onboardingViewModel)
        }
    }
}
