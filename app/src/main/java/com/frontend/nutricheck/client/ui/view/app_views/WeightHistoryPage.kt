package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.ui.view_model.navigation.NavigationActions
import com.frontend.nutricheck.client.ui.view_model.dashboard.weight_history.WeightHistoryViewModel

@Composable
fun WeightHistoryPage(
    modifier: Modifier = Modifier,
    actions: NavigationActions,
    weightHistoryPageViewModel: WeightHistoryViewModel = hiltViewModel(),
    title: String = "Gewichtshistorie",
    onPeriodSelected: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {

}