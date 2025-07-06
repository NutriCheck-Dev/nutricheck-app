package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.ui.view_model.HistoryViewModel
import com.frontend.nutricheck.client.ui.view_model.navigation.NavigationActions

@Composable
fun HistoryPage(
    modifier: Modifier = Modifier,
    actions: NavigationActions,
    //viewModel: HistoryViewModel = hiltViewModel(),
    onSwitchClick: (String) -> Unit = {}
) {}