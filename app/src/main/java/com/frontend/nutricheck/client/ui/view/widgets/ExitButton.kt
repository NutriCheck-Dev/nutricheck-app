package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A composable function that displays an exit button.
 */
@Composable
fun ExitButton(
    onBack: () -> Unit = {},
    modifier : Modifier
) {
    val colors = MaterialTheme.colorScheme

    IconButton(
        onClick = { onBack() },
        modifier = modifier
            .background(
                color = colors.onSurfaceVariant,
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Exit",
            tint = colors.surface
        )
    }
}
