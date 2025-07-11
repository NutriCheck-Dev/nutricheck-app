package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@Composable
fun CustomDetailsButton() {
    IconButton(
        onClick = {}
    ) {
        Icon(
            imageVector = Icons.Filled.MoreHoriz,
            contentDescription = "Details"
        )
    }
}