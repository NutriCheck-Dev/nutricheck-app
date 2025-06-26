package com.frontend.nutricheck.client.ui.view.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ActionConfirmation(
    modifier: Modifier = Modifier,
    title: String = "",
    description: String = "",
    confirmText: String = "Bestätigen",
    cancelText: String = "Abbrechen",
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {}