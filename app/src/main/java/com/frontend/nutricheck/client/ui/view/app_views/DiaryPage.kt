package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import com.frontend.nutricheck.client.ui.view_model.HistoryViewModel
import com.frontend.nutricheck.client.ui.view_model.navigation.NavigationActions
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.frontend.nutricheck.client.ui.view_model.RecipeOverviewViewModel
import dagger.hilt.android.AndroidEntryPoint

@Composable
fun DiaryPage(
    modifier: Modifier = Modifier,
    actions: NavigationActions,
    historyViewModel: HistoryViewModel = hiltViewModel(),
    recipeOverviewViewModel: RecipeOverviewViewModel,
    navEntry: NavBackStackEntry
) {

}