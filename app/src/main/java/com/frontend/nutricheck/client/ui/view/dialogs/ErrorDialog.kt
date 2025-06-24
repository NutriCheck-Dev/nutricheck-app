package com.frontend.nutricheck.client.ui.view.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ErrorDialog(
    modifier: Modifier = Modifier,
    title: String = "Error",
    message: String = "An unexpected error occurred.",
    onDismissRequest: () -> Unit = {}
) {

}