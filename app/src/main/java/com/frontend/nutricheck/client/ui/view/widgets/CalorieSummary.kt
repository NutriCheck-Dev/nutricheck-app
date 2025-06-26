package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.ui.view_model.HistoryViewModel

// This file defines a composable function for displaying a calorie summary widget.
@Composable
fun CalorieSummary(
    modifier: Modifier = Modifier,
    historyViewModel: HistoryViewModel = hiltViewModel(),
    title: String = "Verbleibende Kalorien",
    goalCalories: Int = 2000,
    consumedCalories: Int = 1500,
    remainingCalories: Int = 500,
    onClick: () -> Unit = {}
) {

}