package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.ui.view_model.dashboard.daily_calories.DailyCalorieViewModel

//This file represents a placeholder for the CaloriesToday widget.
@Composable
fun CaloriesToday(
    modifier: Modifier = Modifier,
    dailyCalorieViewModel: DailyCalorieViewModel = hiltViewModel(),
    title: String = "Heute",
    calories: Int = 0,
    subtitle: String = "kcal"
) {

}