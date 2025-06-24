package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// ChartRangeSwitcher is a composable that allows users to switch between different time periods for chart data.
@Composable
fun ChartRangeSwitcher(
    modifier: Modifier = Modifier,
    firstPeriod: String? = "",
    secondPeriod: String? = "",
    thirdPeriod: String? = "",
    onPeriodSelected: (String) -> Unit = {}
) {

}