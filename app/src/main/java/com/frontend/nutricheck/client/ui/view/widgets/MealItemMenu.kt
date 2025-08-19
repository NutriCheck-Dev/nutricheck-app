package com.frontend.nutricheck.client.ui.view.widgets

import com.frontend.nutricheck.client.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource

@Composable
fun MealItemMenu(
    onRemove: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme

    IconButton(onClick = { showDialog = true }) {
        Icon(
            Icons.Default.Close,
            contentDescription = stringResource(R.string.meal_item_menu_title),
            tint = colors.onSurfaceVariant
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.meal_item_menu_title)) },
            text = { Text(stringResource(R.string.meal_item_menu_text)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onRemove()
                    }
                ) {
                    Text(stringResource(R.string.meal_item_menu_remove))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.meal_item_menu_cancel))
                }
            }
        )
    }
}
