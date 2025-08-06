package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

/**
 * A button that navigates back to the previous screen.
 *
 * @param onBack Callback function to be invoked when the button is clicked.
 */
@Composable
fun NavigateBackButton(
    onBack: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    IconButton(
        onClick = { onBack() },
        modifier = Modifier.clip(CircleShape)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = colors.onSurfaceVariant
        )
    }
}