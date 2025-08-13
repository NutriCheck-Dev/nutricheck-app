package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.ui.view.widgets.CustomPersistButton
import com.frontend.nutricheck.client.ui.view.widgets.MealSelector
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.FoodSearchViewModel
import com.frontend.nutricheck.client.ui.view_model.SearchEvent
import com.frontend.nutricheck.client.ui.view_model.SearchUiState

@Composable
fun CreateMealPage(
    modifier: Modifier = Modifier,
    searchViewModel: FoodSearchViewModel,
    onItemClick: (FoodComponent) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val uiState by searchViewModel.uiState.collectAsState()
    val searchState by searchViewModel.searchState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(searchViewModel) {
        searchViewModel.events.collect { event ->
            if (event is SearchEvent.MealSaved) {
                snackbarHostState.showSnackbar(
                    message = context.getString(R.string.meal_logged_success),
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            MealSelector(
                dayTime = (searchState as? SearchUiState.AddComponentsToMealState)?.dayTime,
                expanded = searchState.parameters.expanded,
                onExpandedChange = { searchViewModel.onEvent(SearchEvent.MealSelectorClick) },
                trailingContent = {
                    CustomPersistButton {
                        searchViewModel.onEvent(SearchEvent.SubmitComponentsToMeal)
                    }
                                  },
                onBack = { onBack() },
                onMealSelected = { daytime ->
                    searchViewModel.onEvent(SearchEvent.DayTimeChanged(daytime))
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if(uiState is BaseViewModel.UiState.Error) {
                item {
                    ShowErrorMessage(
                        error = (uiState as BaseViewModel.UiState.Error).message,
                        onClick = {
                            searchViewModel.onEvent(SearchEvent.ResetErrorState)
                        }
                    )
                }
            }
            item {
                SearchPage(
                    onItemClick = { onItemClick(it) },
                    expand = searchState.parameters.expanded,
                    addedComponents = searchState.parameters.addedComponents.map { it },
                    query = searchState.parameters.query,
                    searchResults = if (searchState.parameters.selectedTab == 0) searchState.parameters.generalResults
                    else searchState.parameters.localRecipesResults,
                    onSearchClick = { searchViewModel.onEvent(SearchEvent.Search) },
                    onQueryChange = { searchViewModel.onEvent(SearchEvent.QueryChanged(it)) },
                    addFoodComponent = {
                        searchViewModel.onEvent(SearchEvent.AddFoodComponent(it))
                    },
                    removeFoodComponent = { foodComponent ->
                        searchViewModel.onEvent(SearchEvent.RemoveFoodComponent(foodComponent))
                    },
                    selectedTab = searchState.parameters.selectedTab,
                    onSelectTab = { index ->
                        when (index) {
                            0 -> searchViewModel.onEvent(SearchEvent.ClickSearchAll)
                            1 -> searchViewModel.onEvent(SearchEvent.ClickSearchMyRecipes)
                        }
                    },
                    isLoading = uiState == BaseViewModel.UiState.Loading,
                    showEmptyState = searchState.parameters.hasSearched &&
                    searchState.parameters.lastSearchedQuery == searchState.parameters.query
                )
            }
        }
    }
}

@Composable
private fun ShowErrorMessage(
    title: String = stringResource(R.string.show_error_message_title),
    error: String,
    onClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onClick() },
        title = { Text(title) },
        text = { Text(error) },
        confirmButton = {
            Button(onClick = { onClick() }) {
                Text(stringResource(R.string.label_ok))
            }
        }
    )
}