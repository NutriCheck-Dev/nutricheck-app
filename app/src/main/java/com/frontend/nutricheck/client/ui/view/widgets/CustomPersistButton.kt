package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.frontend.nutricheck.client.R

/**
 * A custom persist button that can be used in various parts of the application.
 *
 * @param onClick A lambda function that is called when the button is clicked.
 */
@Composable
fun CustomPersistButton(onClick: () -> Unit = {}) {
    val colors = MaterialTheme.colorScheme
    IconButton(
        onClick = { onClick() },
        modifier = Modifier
            .clip(CircleShape)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = stringResource(R.string.save),
            tint = colors.onSurfaceVariant
        )
    }
}