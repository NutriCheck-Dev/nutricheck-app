package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.ui.view.widgets.BottomSheetSearchContent
import com.frontend.nutricheck.client.ui.view.widgets.CustomAddButton
import com.frontend.nutricheck.client.ui.view.widgets.CustomPersistButton
import com.frontend.nutricheck.client.ui.view.widgets.MealSelector
import com.frontend.nutricheck.client.ui.view.widgets.SheetScaffold
import com.frontend.nutricheck.client.ui.view.widgets.ShowErrorMessage
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
    val colors = MaterialTheme.colorScheme

    SheetScaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
        showSheet = searchState.parameters.bottomSheetExpanded,
        onSheetHidden = { searchViewModel.onEvent(SearchEvent.HideBottomSheet) },
        topBar = {
            MealSelector(
                dayTime = (searchState as? SearchUiState.AddComponentsToMealState)?.dayTime,
                expanded = searchState.parameters.mealSelectorExpanded,
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
        },
        sheetContent = {
            BottomSheetSearchContent(
                foodComponents = if (searchState.parameters.selectedTab == 0) searchState.parameters.generalResults
                else searchState.parameters.localRecipesResults,
                selectedTab = searchState.parameters.selectedTab,
                onSelectTab = { index ->
                    when (index) {
                        0 -> searchViewModel.onEvent(SearchEvent.ClickSearchAll)
                        1 -> searchViewModel.onEvent(SearchEvent.ClickSearchMyRecipes)
                    }
                },
                trailingContent = { item ->
                    CustomAddButton {
                        searchViewModel.onEvent(SearchEvent.AddFoodComponent(item))
                        searchViewModel.onEvent(SearchEvent.Clear)
                    }
                                  },
                onItemClick = { onItemClick(it) },
                query = searchState.parameters.query,
                onQueryChange = { searchViewModel.onEvent(SearchEvent.QueryChanged(it)) },
                onSearch = { searchViewModel.onEvent(SearchEvent.Search) },
                showTabRow = true,
                isLoading = uiState == BaseViewModel.UiState.Loading,
                showEmptyState = searchState.parameters.hasSearched &&
                        searchState.parameters.lastSearchedQuery == searchState.parameters.query
            )
        }
    ) { innerPadding ->
        val direction = LocalLayoutDirection.current

        val noBottomPadding = PaddingValues(
            start = innerPadding.calculateStartPadding(direction),
            top = innerPadding.calculateTopPadding(),
            end = innerPadding.calculateEndPadding(direction),
            bottom = 0.dp
        )
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(noBottomPadding)
                .padding(16.dp)
        ) {
            if (uiState is BaseViewModel.UiState.Error) {
                item {
                    ShowErrorMessage(
                        error = (uiState as BaseViewModel.UiState.Error).message,
                        onClick = { searchViewModel.onEvent(SearchEvent.ResetErrorState) }
                    )
                }
            }
            item {
                SearchPage(
                    onItemClick = { onItemClick(it) },
                    showBottomSheet = { searchViewModel.onEvent(SearchEvent.ShowBottomSheet) },
                    removeFoodComponent = { searchViewModel.onEvent(SearchEvent.RemoveFoodComponent(it)) },
                    addedComponents = searchState.parameters.addedComponents
                    )
            }
        }
    }
}