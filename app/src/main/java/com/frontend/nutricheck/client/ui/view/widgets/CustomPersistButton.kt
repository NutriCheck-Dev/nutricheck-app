package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CustomPersistButton(onClick: () -> Unit = {}) {
    val colors = MaterialTheme.colorScheme
    IconButton(
        onClick = { onClick() },
        modifier = Modifier
            .background(
                color = colors.onSurfaceVariant,
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Persist",
            tint = colors.surface
        )
    }
}