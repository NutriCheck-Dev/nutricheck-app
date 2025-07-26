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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.ui.theme.AppTheme
import com.frontend.nutricheck.client.ui.theme.extended
import com.frontend.nutricheck.client.ui.view_model.recipe.report.ReportRecipeViewModel

@Composable
fun ReportRecipeDialog(
    reportRecipeViewModel: ReportRecipeViewModel,
    title: String = "Report",
    confirmText: String = "Send",
    cancelText: String = "Cancel",
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {},
    onDismiss: () -> Unit = {},
    onValueChange: (String) -> Unit = {},
    reportText: String = "This is a test report."
) {
    val colors = MaterialTheme.colorScheme
    val extendedColors = MaterialTheme.extended
    MaterialTheme.typography

    AlertDialog(
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
                    value = reportText,
                    placeholder = {
                        Text(
                            text = "Please describe the issue with this recipe.",
                        )
                    },
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        onDismissRequest = onDismiss,
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
