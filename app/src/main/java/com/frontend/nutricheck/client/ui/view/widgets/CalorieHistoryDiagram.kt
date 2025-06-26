package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.ui.view_model.HistoryViewModel

@Composable
fun CalorieHistoryDiagram(
    modifier: Modifier = Modifier,
    historyViewModel: HistoryViewModel = hiltViewModel(),
    title: String = "Kalorienverlauf",
    firstPeriod: String? = "",
    secondPeriod: String? = "",
    thirdPeriod: String? = "",
    onPeriodSelected: (String) -> Unit = {},
) {}