package com.frontend.nutricheck.client.ui.view.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.flags.SemanticsTags
import com.frontend.nutricheck.client.ui.theme.extended

/**
 * A composable function that displays a dialog for reporting a recipe.
 *
 * @param title The title of the dialog.
 * @param confirmText The text for the confirm button.
 * @param cancelText The text for the cancel button.
 * @param onConfirm Callback function to be invoked when the confirm button is clicked.
 * @param onCancel Callback function to be invoked when the cancel button is clicked.
 * @param onDismiss Callback function to be invoked when the dialog is dismissed.
 * @param onValueChange Callback function to handle changes in the input text field.
 * @param inputText The current text in the input field.
 * @param reportTextPlaceholder Placeholder text for the input field.
 */
@Composable
fun ReportRecipeDialog(
    title: String = "Report",
    confirmText: String = "Send",
    cancelText: String = "Cancel",
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {},
    onDismiss: () -> Unit = {},
    onValueChange: (String) -> Unit = {},
    inputText: String,
    reportTextPlaceholder: String = "This is a test report."
) {
    val colors = MaterialTheme.colorScheme
    val extendedColors = MaterialTheme.extended
    MaterialTheme.typography

    AlertDialog(
        modifier = Modifier.semantics { contentDescription = SemanticsTags.REPORT_DIALOG },
        icon = {
            Icon(imageVector = Icons.Default.Report,
                contentDescription = null) },
        title = { Text(text = title) },
        text = {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, colors.outline)
            ) {
                TextField(
                    value = inputText,
                    placeholder = {
                        Text(
                            text = reportTextPlaceholder,
                        )
                    },
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = SemanticsTags.REPORT_DIALOG_INPUT }
                )
            }
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                modifier = Modifier.semantics { contentDescription = SemanticsTags.REPORT_DIALOG_CONFIRM },
                onClick = onConfirm
            ) {
                Text(text = confirmText, color = extendedColors.confirmation.color)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = cancelText, color = colors.error)
            }
        }
    )
}
