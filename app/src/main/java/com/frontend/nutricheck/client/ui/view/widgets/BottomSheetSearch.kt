package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetSearch(
    foodComponents: List<FoodComponent>,
    trailingContent: @Composable ((FoodComponent) -> Unit)?,
    showBottomSheet: Boolean,
    onDismiss: () -> Unit,
    onItemClick: (FoodComponent) -> Unit,
    query: String = "",
    onQueryChange: (String) -> Unit = {},
    onSearch: () -> Unit = {}
) {

    val scrollState = rememberScrollState()

    if (showBottomSheet) {
    ModalBottomSheet(
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

                    )

                CustomTabRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    options = listOf("Alle", "Meine Rezepte"),
                    selectedOption = 1
                )

                LazyColumn (
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    item {
                        FoodComponentList(
                            foodComponents = foodComponents,
                            trailingContent = { foodComponent ->
                                trailingContent?.invoke(
                                    foodComponent
                                )
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