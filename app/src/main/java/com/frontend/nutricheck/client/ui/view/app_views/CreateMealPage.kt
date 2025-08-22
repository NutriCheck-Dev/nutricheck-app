package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.ui.view.widgets.CustomPersistButton
import com.frontend.nutricheck.client.ui.view.widgets.MealSelector
import com.frontend.nutricheck.client.ui.view.widgets.ShowErrorMessage
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.FoodSearchViewModel
import com.frontend.nutricheck.client.ui.view_model.SearchEvent
import com.frontend.nutricheck.client.ui.view_model.SearchUiState

/**
 * A composable function that represents the Create Meal page.
 * This page allows users to search for food components,
 * add them to a meal, and select the meal time.
 */
@Composable
fun CreateMealPage(
    modifier: Modifier = Modifier,
    searchViewModel: FoodSearchViewModel,
    onItemClick: (FoodComponent) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val uiState by searchViewModel.uiState.collectAsState()
    val searchState by searchViewModel.searchState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
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
                onBack = {
                    onBack()
                    searchViewModel.onEvent(SearchEvent.Clear)
                         },
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
                    searchState.parameters.lastSearchedQuery == searchState.parameters.query,
                    toggleExpand = { searchViewModel.onEvent(SearchEvent.ExpandBottomSheet) }
                )
            }
        }
    }
}