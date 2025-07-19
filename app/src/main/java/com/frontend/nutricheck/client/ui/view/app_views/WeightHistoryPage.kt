package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.Weight
import com.frontend.nutricheck.client.ui.view_model.profile.ProfileEvent
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightHistoryPage(
    // Die Signatur wurde angepasst, um eine Liste von Weight-Objekten zu erhalten
    weightState: List<Weight>,
    onEvent: (ProfileEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.profile_menu_item_weight_history)) },
                navigationIcon = {
                    // Back-Button oben links
                    IconButton(onClick = { onEvent(ProfileEvent.DisplayProfileOverview) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_button_description)
                        )
                    }
                },
                actions = {
                    // Plus-Button oben rechts
                    IconButton(onClick = { /* onEvent(ProfileEvent.AddNewWeight) */ }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(id = R.string.add_weight_entry_description)
                        )
                    }
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
                Divider()
            }
        }
    }
}

@Composable
private fun WeightHistoryItem(weightEntry: Weight) {
    // Formatiert das Datum für die Anzeige
    val dateFormat = SimpleDateFormat("dd. MMMM yyyy", Locale.getDefault())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Linke Seite: Datum
        Text(
            text =  "25.24.2025",//dateFormat.format(weightEntry.date),
            style = MaterialTheme.typography.bodyLarge
        )
        // Rechte Seite: Gewicht
        Text(
            text = "${weightEntry.value} kg",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview
@Composable
fun WeightHistoryPagePreview() {
    // Beispiel-Daten für die Vorschau
    val sampleWeights = listOf(
        Weight(date = System.currentTimeMillis(), value = 70.0),
        Weight(date = System.currentTimeMillis() - 86400000, value = 69.5),
        Weight(date = System.currentTimeMillis() - 172800000, value = 69.0)
    )

    WeightHistoryPage(weightState = sampleWeights, onEvent = {})
}