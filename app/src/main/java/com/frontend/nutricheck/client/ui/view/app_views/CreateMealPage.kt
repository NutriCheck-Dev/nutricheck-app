package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.ui.view.widgets.CustomPersistButton
import com.frontend.nutricheck.client.ui.view.widgets.MealSelector
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.search_food_product.FoodSearchViewModel
import com.frontend.nutricheck.client.ui.view_model.search_food_product.SearchEvent
import com.frontend.nutricheck.client.ui.view_model.search_food_product.SearchUiState

@Composable
fun CreateMealPage(
    modifier: Modifier = Modifier,
    searchViewModel: FoodSearchViewModel,
    onItemClick: (FoodComponent) -> Unit = {},
    onConfirm: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val uiState by searchViewModel.uiState.collectAsState()
    val searchState by searchViewModel.searchState.collectAsState()


    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MealSelector(
                dayTime = if (searchState is SearchUiState.AddComponentsToMealState)
                    (searchState as SearchUiState.AddComponentsToMealState).dayTime
                else null,
                expanded = searchState.parameters.expanded,
                onExpandedChange = { searchViewModel.onEvent(SearchEvent.MealSelectorClick) },
                trailingContent = {
                    CustomPersistButton {
                        searchViewModel.onEvent(SearchEvent.SubmitComponentsToMeal)
                        onConfirm()
                    }
                                  },
                onBack = { onBack() },
                onMealSelected = { daytime ->
                    searchViewModel.onEvent(SearchEvent.DayTimeChanged(daytime))
                }
            )
        }
    ) { paddingValues ->

        when (uiState) {
            BaseViewModel.UiState.Loading -> {
            }
            is BaseViewModel.UiState.Error -> {
                val errorMessage = (uiState as BaseViewModel.UiState.Error).message
            }
            BaseViewModel.UiState.Ready -> {
                SearchPage(
                    modifier = modifier.padding(paddingValues),
                    onItemClick = { onItemClick(it) },
                    expand = searchState.parameters.expanded,
                    addedComponents = searchState.parameters.addedComponents.map { it.second },
                    query = searchState.parameters.query,
                    searchResults = searchState.parameters.results,
                    onSearchClick = { searchViewModel.onEvent(SearchEvent.Search) },
                    onQueryChange = { searchViewModel.onEvent(SearchEvent.QueryChanged(it)) },
                    addFoodComponent = {
                        searchViewModel.onEvent(SearchEvent.AddFoodComponent(Pair(1.0, it)))
                    },
                    removeFoodComponent = { foodComponent ->
                        searchViewModel.onEvent(SearchEvent.RemoveFoodComponent(foodComponent))},
                    selectedTab = searchState.parameters.selectedTab,
                    onSelectTab = { index ->
                        when (index) {
                            0 -> searchViewModel.onEvent(SearchEvent.ClickSearchAll)
                            1 -> searchViewModel.onEvent(SearchEvent.ClickSearchMyRecipes)
                        }
                    },
                    isLoading = uiState == BaseViewModel.UiState.Loading
                )
            }
        }
    }
}