package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.ui.view_model.HistoryViewModel

// This file defines a DateSelector composable function that allows users to select a date.
@Composable
fun DateSelector(
    modifier: Modifier = Modifier,
    historyViewModel: HistoryViewModel = hiltViewModel(),
    selectedDate: String = "Today",
    onDateSelected: (String) -> Unit = {}
) {

}