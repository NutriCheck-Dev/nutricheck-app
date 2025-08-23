package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent

/**
 * A composable function that displays a bottom sheet for searching food components.
 * It includes a search bar, tab row for filtering results, and a list of food components.
 *
 * @param foodComponents The list of food components to display in the bottom sheet.
 * @param selectedTab The currently selected tab index.
 * @param onSelectTab Callback function to handle tab selection changes.
 * @param trailingContent Optional trailing content to display alongside each food component.
 * @param showBottomSheet Whether to show the bottom sheet.
 * @param onDismiss Callback function to handle bottom sheet dismissal.
 * @param onItemClick Callback function to handle item clicks in the food component list.
 * @param query The current search query.
 * @param onQueryChange Callback function to handle changes in the search query.
 * @param onSearch Callback function to handle search button clicks.
 * @param showTabRow Whether to show the tab row for selecting different categories.
 * @param isLoading Whether the search results are currently loading.
 * @param showEmptyState Whether to show an empty state when there are no search results.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetSearch(
    foodComponents: List<FoodComponent>,
    selectedTab: Int,
    onSelectTab: (Int) -> Unit,
    trailingContent: @Composable ((FoodComponent) -> Unit)?,
    showBottomSheet: Boolean,
    onDismiss: () -> Unit,
    onItemClick: (FoodComponent) -> Unit,
    query: String = "",
    onQueryChange: (String) -> Unit = {},
    onSearch: () -> Unit = {},
    showTabRow: Boolean = true,
    isLoading: Boolean = false,
    showEmptyState: Boolean = false,
) {

    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier.testTag(stringResource(R.string.androidtest_tag_ingredient_search_sheet)),
            onDismissRequest = { onDismiss() },
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .heightIn(
                        min = 0.dp,
                        max = with(LocalConfiguration.current) {
                            (screenHeightDp.dp * 0.75f)
                        }
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    FoodComponentSearchBar(
                        query = query,
                        onQueryChange = { onQueryChange(it) },
                        onSearch = { onSearch() },
                        modifier = Modifier.testTag(stringResource(R.string.androidtest_tag_ingredient_search_query))
                        )

                    if (showTabRow) {
                        val option1 = stringResource(R.string.search_tab_all)
                        val option2 = stringResource(R.string.search_tab_my_recipes)
                        CustomTabRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            options = listOf(option1, option2),
                            selectedOption = selectedTab,
                            onSelect = { onSelectTab(it) }
                        )
                    }

                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        showEmptyState && foodComponents.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.error_search_result_empty),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                            ) {
                                item {
                                    FoodComponentList(
                                        foodComponents = foodComponents,
                                        trailingContent = { foodComponent ->
                                            trailingContent?.invoke(foodComponent)
                                        },
                                        onItemClick = { foodComponent -> onItemClick(foodComponent) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}