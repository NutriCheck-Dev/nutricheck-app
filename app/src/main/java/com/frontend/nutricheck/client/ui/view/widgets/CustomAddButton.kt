package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.frontend.nutricheck.client.R

/**
 * A custom add button that can be used in various parts of the application.
 *
 * @param onClick A lambda function that is called when the button is clicked.
 */
@Composable
fun CustomAddButton(onClick: () -> Unit = {}) {
    val colors = MaterialTheme.colorScheme
    IconButton(
        onClick = { onClick() },
        modifier = Modifier
            .background(
                color = colors.onSurfaceVariant,
                shape = CircleShape
            ),
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.navigation_bar_label_add),
            tint = colors.surface
        )
    }
}