package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
fun Onboarding(
    navController: NavHostController = rememberNavController(),
    onboardingViewModel: OnboardingViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = Unit) {
        onboardingViewModel.events.collect { event ->
            when (event) {
                is OnboardingEvent.NavigateToName ->
                    navController.navigate(OnboardingScreen.Name.route)
                is OnboardingEvent.NavigateToBirthdate ->
                    navController.navigate(OnboardingScreen.Birthdate.route)
                is OnboardingEvent.NavigateToGender ->
                    navController.navigate(OnboardingScreen.Gender.route)
                is OnboardingEvent.NavigateToHeight ->
                    navController.navigate(OnboardingScreen.Height.route)
                is OnboardingEvent.NavigateToWeight ->
                    navController.navigate(OnboardingScreen.Weight.route)
                is OnboardingEvent.NavigateToSportFrequency ->
                    navController.navigate(OnboardingScreen.SportFrequency.route)
                is OnboardingEvent.NavigateToWeightGoal ->
                    navController.navigate(OnboardingScreen.WeightGoal.route)
                is OnboardingEvent.NavigateToTargetWeight ->
                    navController.navigate(OnboardingScreen.TargetWeight.route)
                else -> { /* Andere Events ignorieren */ }
            }
            TODO("add Dashboard navigation")
        }
    }

    NavHost(
        navController = navController,
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
            OnboardingTargetWeight(onboardingViewModel) }
    }
}
