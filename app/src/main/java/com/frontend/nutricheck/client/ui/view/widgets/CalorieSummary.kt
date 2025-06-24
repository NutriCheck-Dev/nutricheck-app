package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CalorieSummary(
    modifier: Modifier = Modifier,
    title: String = "Verbleibende Kalorien",
    goalCalories: Int = 2000,
    consumedCalories: Int = 1500,
    remainingCalories: Int = 500,
    onClick: () -> Unit = {}
) {

}