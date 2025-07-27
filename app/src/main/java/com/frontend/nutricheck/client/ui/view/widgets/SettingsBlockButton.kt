package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SettingsBlockButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1F1F1F))
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 8.dp, vertical = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
            subtitle?.let {
                Text(
                    text = it,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        trailingContent?.invoke()
    }
}

@Preview
@Composable
fun SettingsBlockButtonPreview() {
    SettingsBlockButton(
        icon = Icons.Default.Person,
        title = "Persönliche Daten & Ziel",
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White
            )
        },
        onClick = { /* navigate */ }
    )
}
