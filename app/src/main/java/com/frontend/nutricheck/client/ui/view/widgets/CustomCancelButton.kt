package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.material3.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

@Composable
fun CustomCloseButton(onClick: () -> Unit = {}) {
    val colors = MaterialTheme.colorScheme

    IconButton(
        onClick = { onClick() },
        modifier = Modifier.clip(CircleShape)
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Cancel",
            tint = colors.surface
        )
    }
}