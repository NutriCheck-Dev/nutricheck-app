package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.ui.view.widgets.BottomSheetSearch
import com.frontend.nutricheck.client.ui.view.widgets.CustomAddButton
import com.frontend.nutricheck.client.ui.view.widgets.CustomCloseButton
import com.frontend.nutricheck.client.ui.view.widgets.FoodComponentList
import kotlinx.coroutines.launch

/**
 * A composable function that displays a search page for food components.
 * It allows users to search for food components, add them to a list, and remove them as needed.
 *
 * @param modifier The modifier to be applied to the root composable.
 * @param selectedTab The currently selected tab index.
 * @param onSelectTab Callback function to handle tab selection changes.
 * @param onItemClick Callback function to handle item clicks in the food component list.
 * @param query The current search query.
 * @param onSearchClick Callback function to handle search button clicks.
 * @param onQueryChange Callback function to handle changes in the search query.
 * @param addFoodComponent Callback function to add a food component to the list.
 * @param removeFoodComponent Callback function to remove a food component from the list.
 * @param addedComponents The list of food components that have been added.
 * @param searchResults The list of food components that match the search query.
 * @param expand Whether to expand the bottom sheet for searching food components.
 * @param showTabRow Whether to show the tab row for selecting different categories.
 * @param isLoading Whether the search results are currently loading.
 * @param showEmptyState Whether to show an empty state when there are no search results.
 */
@Composable
fun SearchPage(
    modifier: Modifier = Modifier,
    selectedTab: Int = 0,
    onSelectTab: (Int) -> Unit = {},
    onItemClick: (FoodComponent) -> Unit = {},
    query: String,
    onSearchClick: () -> Unit,
    onQueryChange: (String) -> Unit,
    addFoodComponent: (FoodComponent) -> Unit,
    removeFoodComponent: (FoodComponent) -> Unit,
    addedComponents: List<FoodComponent>,
    searchResults: List<FoodComponent>,
    expand: Boolean = false,
    showTabRow: Boolean = true,
    isLoading: Boolean,
    showEmptyState: Boolean
) {
    val styles = MaterialTheme.typography
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(expand) }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = stringResource(R.string.label_ingredients),
            style = styles.titleMedium
        )

        FoodComponentList(
            foodComponents = addedComponents,
            trailingContent = { item ->
                CustomCloseButton {
                    removeFoodComponent(item)
                }
            },
            onItemClick = { item ->
                onItemClick(item)
            },
            editing = true,
            onAddButtonClick = {
                showBottomSheet = true
                scope.launch { sheetState.show() }
            }
        )

        BottomSheetSearch(
            query = query,
            onSearch = { onSearchClick() },
            onQueryChange = { onQueryChange(it) },
            foodComponents = searchResults,
            trailingContent = { item ->
                CustomAddButton {
                    addFoodComponent(item)
                    showBottomSheet = false
                    scope.launch { sheetState.hide() }
                }
            },
            onItemClick = { item ->
                onItemClick(item)
            },
            showBottomSheet = showBottomSheet,
            onDismiss = {
                showBottomSheet = false
                scope.launch { sheetState.hide() }
            },
            selectedTab = selectedTab,
            onSelectTab = { onSelectTab(it) },
            showTabRow = showTabRow,
            isLoading = isLoading,
            showEmptyState = showEmptyState
        )
    }
}
