package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.ui.view_model.HistoryViewModel
import com.frontend.nutricheck.client.ui.view_model.dashboard.dashboard_daily_cal.DailyCalorieViewModel
import com.frontend.nutricheck.client.ui.view_model.navigation.NavigationActions

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    actions: NavigationActions,
    calorieHistoryViewModel: HistoryViewModel = hiltViewModel(),
    dailyCalorieViewModel: DailyCalorieViewModel = hiltViewModel(),
    weightHistoryViewModel: HistoryViewModel = hiltViewModel(),
    onPeriodSelectedClick: (String) -> Unit = {}
) {

}