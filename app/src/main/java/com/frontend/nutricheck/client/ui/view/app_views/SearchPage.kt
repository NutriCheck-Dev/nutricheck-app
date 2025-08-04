package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.ui.view.widgets.BottomSheetSearch
import com.frontend.nutricheck.client.ui.view.widgets.CustomAddButton
import com.frontend.nutricheck.client.ui.view.widgets.CustomCloseButton
import com.frontend.nutricheck.client.ui.view.widgets.FoodComponentList
import kotlinx.coroutines.launch

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
) {
    val styles = MaterialTheme.typography
    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(expand) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
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
                }
            },
            onItemClick = { item ->
                onItemClick(item)
            },
            showBottomSheet = showBottomSheet,
            onDismiss = {
                showBottomSheet = false
                scope.launch { sheetState.show() }
            },
            selectedTab = selectedTab,
            onSelectTab = { onSelectTab(it) },
            showTabRow = showTabRow,
            isLoading = isLoading,
        )
    }
}
