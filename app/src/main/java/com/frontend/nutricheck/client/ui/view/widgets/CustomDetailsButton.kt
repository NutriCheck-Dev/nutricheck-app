package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun CustomEditButton() {
    IconButton(
        onClick = {}
    ) {
        Icon(
            imageVector = Icons.Filled.MoreHoriz,
            contentDescription = "Details",
            tint = Color(0xFFF5F5F5)
        )
    }
}