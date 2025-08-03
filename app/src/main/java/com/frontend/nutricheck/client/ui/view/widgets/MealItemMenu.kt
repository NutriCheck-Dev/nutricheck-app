package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*

@Composable
fun MealItemMenu(
    onRemove: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme
    IconButton(onClick = { showDialog = true }) {
        Icon(Icons.Default.MoreVert, contentDescription = "Mehr Optionen",  tint = colors.onSurfaceVariant)

    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Aktion auswählen") },
            text = { Text("Möchtest du dieses Gericht entfernen?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onRemove()
                }) {
                    Text("Entfernen")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Abbrechen")
                }
            }
        )
    }
}
