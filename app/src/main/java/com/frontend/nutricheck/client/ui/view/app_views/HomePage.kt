package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.frontend.nutricheck.client.ui.view.widgets.CalorieHistoryDiagram
import com.frontend.nutricheck.client.ui.view.widgets.CaloriesToday
import com.frontend.nutricheck.client.ui.view.widgets.NutrientBreakdown
import com.frontend.nutricheck.client.ui.view.widgets.WeightHistoryDiagram
import com.frontend.nutricheck.client.ui.view_model.HistoryViewModel
import com.frontend.nutricheck.client.ui.view_model.dashboard.daily_calories.DailyCalorieViewModel
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
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Color.Black)
            .padding(horizontal = 7.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Abstand zwischen den Boxen
        ) {
            CaloriesToday(
                modifier = Modifier
                    .fillMaxSize().weight(1f),
                dailyCalorieViewModel = dailyCalorieViewModel,
                calories = 0,
                subtitle = "kcal",
                calorieValue = 850,           // Beispielwert
                calorieGoal = 2000             // Beispielwert für Füllstand
            )
            NutrientBreakdown(nutrients = mapOf(
                "Eiweiß" to (20 to 146),
                "Kohlenhydrate" to (220 to 300),
                "Fett" to (58 to 90)
            ),
                modifier = Modifier
                    .fillMaxSize().weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        CalorieHistoryDiagram()
        Spacer(modifier = Modifier.height(14.dp))
        WeightHistoryDiagram()

    }
}

@Preview
@Composable
fun HomePageXDefaultPreview() {
    val navController = rememberNavController()
    HomePage(actions = NavigationActions(navController))
}