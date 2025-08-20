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

@Composable
fun SearchPage(
    modifier: Modifier = Modifier,
    onItemClick: (FoodComponent) -> Unit = {},
    removeFoodComponent: (FoodComponent) -> Unit,
    showBottomSheet: () -> Unit,
    addedComponents: List<FoodComponent>
) {
    val styles = MaterialTheme.typography

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
            onAddButtonClick = { showBottomSheet() }
        )
    }
}
