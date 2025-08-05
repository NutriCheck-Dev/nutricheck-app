package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Weight
import com.frontend.nutricheck.client.ui.view.widgets.CustomAddButton
import com.frontend.nutricheck.client.ui.view.widgets.NavigateBackButton
import com.frontend.nutricheck.client.ui.view.widgets.ViewsTopBar
import com.frontend.nutricheck.client.ui.view_model.ProfileEvent
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * A composable function that displays the weight history as a list.
 *
 * @param weightState The list of [Weight] entries to display.
 * @param onEvent A callback function to send [ProfileEvent]s to the ViewModel.
 * @param onBack A callback function to handle back navigation.
 */
@Composable
fun WeightHistoryPage(
    weightState: List<Weight>,
    onEvent: (ProfileEvent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            ViewsTopBar(
                title = { Text(stringResource(id = R.string.profile_menu_item_weight_history)) },
                navigationIcon = {
                    NavigateBackButton(
                        onBack = { onBack() }
                    )
                },
                actions = {
                    CustomAddButton(
                        onClick = { onEvent(ProfileEvent.OnAddNewWeightClick) }
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(weightState) { weightEntry ->
                WeightHistoryItem(weightEntry = weightEntry)
            }
        }
    }
}

@Composable
private fun WeightHistoryItem(weightEntry: Weight) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text =  dateFormat.format(weightEntry.date),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "${weightEntry.value} kg",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
