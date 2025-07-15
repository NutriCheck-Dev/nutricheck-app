package com.frontend.nutricheck.client.ui.view.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.frontend.nutricheck.client.ui.theme.AppTheme
import com.frontend.nutricheck.client.ui.theme.extended

@Composable
fun ActionConfirmationDialog(
    title: String = "",
    description: String = "",
    confirmText: String = "Bestätigen",
    cancelText: String = "Abbrechen",
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {},
    icon: ImageVector,
    onDismissRequest: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val extendedColors = MaterialTheme.extended
    AlertDialog(
        icon = { Icon(imageVector = icon, contentDescription = null) },
        title = { Text(text = title) },
        text = { Text(text = description) },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmText, color = extendedColors.confirmation.color)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = cancelText, color = colors.onError)
            }
        }
    )
}

@Preview
@Composable
fun ActionConfirmationPreview() {
    AppTheme(darkTheme = true) {
        ActionConfirmationDialog(
            title = "Aktion Bestätigen",
            description = "Sind Sie sicher, dass Sie diese Aktion ausführen möchten?",
            confirmText = "Ja",
            cancelText = "Nein",
            icon = Icons.Default.Build,
            onConfirm = { /* Handle confirm action */ },
            onCancel = { /* Handle cancel action */ }
        )
    }
}