package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.ui.view.widgets.CalorieHistoryDiagram
import com.frontend.nutricheck.client.ui.view.widgets.CaloriesToday
import com.frontend.nutricheck.client.ui.view.widgets.NutrientBreakdown
import com.frontend.nutricheck.client.ui.view.widgets.WeightHistoryDiagram
import com.frontend.nutricheck.client.ui.view_model.dashboard.CalorieHistoryViewModel
import com.frontend.nutricheck.client.ui.view_model.dashboard.DailyCalorieViewModel
import com.frontend.nutricheck.client.ui.view_model.dashboard.DailyMacrosViewModel
import com.frontend.nutricheck.client.ui.view_model.dashboard.WeightHistoryViewModel

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

    val calorieInterval = when (selectedCalorieRange) {
        "7T" -> 7
        "30T" -> 30
        "60T" -> 60
        else -> 7
    }

    LaunchedEffect(selectedCalorieRange, selectedWeightRange) {
        calorieHistoryViewModel.displayCalorieHistory(calorieInterval)
        weightHistoryViewModel.displayWeightHistory(selectedWeightRange)
    }

    val calorieHistoryState by calorieHistoryViewModel.calorieHistoryState.collectAsState()
    val dailyCalorieState by dailyCalorieViewModel.dailyCalorieState.collectAsState()
    val dailyMacrosState by dailyMacrosViewModel.dailyMacrosState.collectAsState()
    val weightHistoryState by weightHistoryViewModel.weightHistoryState.collectAsState()

    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 7.dp)
    ) {
        Image(
            modifier = Modifier
                .padding(horizontal = 7.dp, vertical = 7.dp)
                .width(79.dp)
                .height(36.dp),
            painter = painterResource(id = R.drawable.nutricheck_logo),
            contentDescription = "Nutricheck Logo",
            colorFilter = ColorFilter.tint(colors.onBackground) // theme-fähig
        )

        Spacer(modifier = Modifier.height(7.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CaloriesToday(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                state = dailyCalorieState
            )

            NutrientBreakdown(
                dailyMacrosState = dailyMacrosState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        CalorieHistoryDiagram(
            modifier = Modifier,
            calorieHistoryState = calorieHistoryState,
            selectedRange = selectedCalorieRange,
            onPeriodSelected = { selectedCalorieRange = it }
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