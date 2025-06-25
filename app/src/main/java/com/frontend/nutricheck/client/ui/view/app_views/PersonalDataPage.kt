package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.ui.view_model.ProfileOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.navigation.NavigationActions
import com.frontend.nutricheck.client.ui.view_model.profile.BaseEditPersonalDataViewModel
import com.frontend.nutricheck.client.ui.view_model.profile.EditPersonalDataViewModel

@Composable
fun PersonalDataPage(
    modifier: Modifier = Modifier,
    actions: NavigationActions,
    title: String = "Persönliche Daten",
    personalDataViewModel: EditPersonalDataViewModel = hiltViewModel(),
    onSaveClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {

}