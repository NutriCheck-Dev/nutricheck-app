package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.frontend.nutricheck.client.R

/**
 * A composable function that displays an error message in a dialog.
 *
 * @param title The title of the dialog, defaults to a string resource.
 * @param error The error message to display.
 * @param onClick The callback function to execute when the dialog's button is clicked.
 */
@Composable
fun ShowErrorMessage(
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.show_error_message_title),
    error: String,
    onClick: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { onClick() },
        title = { Text(title) },
        text = { Text(error) },
        confirmButton = {
            Button(onClick = { onClick() }) {
                Text(stringResource(R.string.label_ok))
            }
        }
    )
}