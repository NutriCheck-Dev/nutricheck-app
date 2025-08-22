package com.frontend.nutricheck.client.ui.view.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Weight
import com.frontend.nutricheck.client.ui.view_model.ProfileEvent
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Dialog to confirm the deletion of a weight entry.
 */
@Composable
fun DeleteWeightDialog(
    selectedWeight: Weight?,
    onDismissRequest: () -> Unit,
    onEvent : (ProfileEvent) -> Unit,
) {
    if (selectedWeight == null) {
        return
    }
    selectedWeight.let { weight ->
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = stringResource(R.string.delete_weight_dialog_title)) },
            text = {
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                Text(
                    text = "${weight.value} kg, ${dateFormat.format(weight.date)}"
                )
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onEvent(ProfileEvent.DeleteWeight)
                    onDismissRequest()
                }) {
                    Text(stringResource(R.string.label_delete))
                }
            },
        )
    }
}
