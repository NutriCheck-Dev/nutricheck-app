package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.ui.view.widgets.CalorieHistoryDiagram
import com.frontend.nutricheck.client.ui.view.widgets.CaloriesToday
import com.frontend.nutricheck.client.ui.view.widgets.NutrientBreakdown
import com.frontend.nutricheck.client.ui.view.widgets.WeightHistoryDiagram
import com.frontend.nutricheck.client.ui.view_model.dashboard.calorie_history.CalorieHistoryViewModel
import com.frontend.nutricheck.client.ui.view_model.dashboard.daily_calories.DailyCalorieViewModel
import com.frontend.nutricheck.client.ui.view_model.dashboard.daily_macros.DailyMacrosViewModel
import com.frontend.nutricheck.client.ui.view_model.dashboard.weight_history.WeightHistoryViewModel

@Composable
fun HomePage(
    calorieHistoryViewModel: CalorieHistoryViewModel,
    dailyCalorieViewModel: DailyCalorieViewModel,
    dailyMacrosViewModel: DailyMacrosViewModel,
    weightHistoryViewModel: WeightHistoryViewModel,
) {
    val scrollState = rememberScrollState()
    var selectedCalorieRange by remember { mutableStateOf("7T") }
    var selectedWeightRange by remember { mutableStateOf("1M") }
    LaunchedEffect(selectedCalorieRange, selectedWeightRange) {
        calorieHistoryViewModel.displayCalorieHistory(selectedCalorieRange)
        weightHistoryViewModel.displayWeightHistory(selectedWeightRange)
    }
    val calorieHistoryState by calorieHistoryViewModel.calorieHistoryState.collectAsState()
    val dailyCalorieState by dailyCalorieViewModel.dailyCalorieState.collectAsState()
    val dailyMacrosState by dailyMacrosViewModel.dailyMacrosState.collectAsState()
    val weightHistoryState by weightHistoryViewModel.weightHistoryState.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Color.Black)
            .padding(horizontal = 7.dp)
    ) {
        Image(
            modifier = Modifier
                .padding(horizontal = 7.dp, vertical = 7.dp)
                .width(79.dp)
                .height(36.dp),
            painter = painterResource(id = R.drawable.nutricheck_logo),
            contentDescription = "Nutricheck Logo",
            colorFilter = ColorFilter.tint(Color.White)
            )
        Spacer(modifier = Modifier.height(7.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Abstand zwischen den Boxen
        ) {
            CaloriesToday(
                modifier = Modifier
                    .fillMaxSize().weight(1f),
                state = dailyCalorieState)
            NutrientBreakdown(
                dailyMacrosState = dailyMacrosState,
                modifier = Modifier
                    .fillMaxSize().weight(1f))
        }
        Spacer(modifier = Modifier.height(14.dp))
        CalorieHistoryDiagram(
            modifier = Modifier,
            calorieHistoryState = calorieHistoryState,
            selectedRange = selectedCalorieRange,
            onPeriodSelected = { selectedCalorieRange = it}
        )
        Spacer(modifier = Modifier.height(14.dp))
        WeightHistoryDiagram(
            modifier = Modifier,
            weightHistoryState = weightHistoryState,
            selectedRange = selectedWeightRange,
            onPeriodSelected = { selectedWeightRange = it },
        )

    }
}