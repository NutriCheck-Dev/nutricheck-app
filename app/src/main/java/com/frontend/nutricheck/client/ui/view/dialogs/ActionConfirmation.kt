package com.frontend.nutricheck.client.ui.view.dialogs

import androidx.compose.runtime.Composable

@Composable
fun ActionConfirmation(
    title: String,
    description: String,
    confirmText: String = "Bestätigen",
    cancelText: String = "Abbrechen",
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {}