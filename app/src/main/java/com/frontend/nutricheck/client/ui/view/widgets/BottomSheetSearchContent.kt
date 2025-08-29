package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent

@Composable
fun BottomSheetSearchContent(
    foodComponents: List<FoodComponent>,
    selectedTab: Int?,
    onSelectTab: (Int) -> Unit,
    trailingContent: @Composable ((FoodComponent) -> Unit)?,
    onItemClick: (FoodComponent) -> Unit,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    showTabRow: Boolean,
    isLoading: Boolean,
    showEmptyState: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        FoodComponentSearchBar(
            query = query,
            onQueryChange = { onQueryChange(it) },
            onSearch = { onSearch() }
        )

        if (showTabRow) {
            val option1 = stringResource(R.string.search_tab_all)
            val option2 = stringResource(R.string.search_tab_my_recipes)
            CustomTabRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                options = listOf(option1, option2),
                selectedOption = selectedTab!!,
                onSelect = { onSelectTab(it) }
            )
        }

        when {
            isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            showEmptyState && foodComponents.isEmpty() -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.error_search_result_empty),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                item {
                    FoodComponentList(
                        foodComponents = foodComponents,
                        trailingContent = { component -> trailingContent?.invoke(component) },
                        onItemClick = { onItemClick(it) }
                    )
                }
            }
        }
    }
}