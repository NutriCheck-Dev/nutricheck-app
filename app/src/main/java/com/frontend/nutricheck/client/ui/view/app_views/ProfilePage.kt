package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.ui.view_model.ProfileOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.navigation.NavigationActions

@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    actions: NavigationActions,
    profilePageViewModel: ProfileOverviewViewModel = hiltViewModel(),
    onPersonalDataClick: () -> Unit = {},
    onWeightHistoryClick: () -> Unit = {},
    onThemeToggleClick: (Boolean) -> Unit = {},
    onLanguageClick: (String) -> Unit = {},
) {

}