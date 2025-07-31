package com.frontend.nutricheck.client.ui.view.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.flags.Language
import com.frontend.nutricheck.client.ui.view_model.ProfileEvent

@Composable
fun ChooseLanguageDialog(
    onEvent : (ProfileEvent) -> Unit,
    currentLanguage: Language,
    onDismissRequest: () -> Unit
) {
    val languages = Language.entries.toTypedArray()
    var selectedLanguage by remember { mutableStateOf(currentLanguage) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(id = R.string.select_language_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                languages.forEach { language ->
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = if (language == selectedLanguage) {
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            ButtonDefaults.buttonColors(
                                containerColor = Color.LightGray,
                                contentColor = Color.Black
                            )
                        },
                        onClick = { selectedLanguage = language }
                    ) {
                        Text(text = language.displayName)
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onEvent(ProfileEvent.SaveLanguage(selectedLanguage))
                onDismissRequest()
            }) {
                Text(stringResource(id = R.string.save))
            }
        }
    )
}