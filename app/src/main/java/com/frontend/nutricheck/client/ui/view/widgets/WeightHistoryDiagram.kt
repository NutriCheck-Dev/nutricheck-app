package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.ui.view_model.weight_history.WeightHistoryViewModel

@Composable
fun WeightHistoryDiagram(
    modifier: Modifier = Modifier,
    weightHistoryViewModel: WeightHistoryViewModel = hiltViewModel(),
    titel: String = "Gewichtshistorie",
    onPeriodSelected: (String) -> Unit = {},
) {}