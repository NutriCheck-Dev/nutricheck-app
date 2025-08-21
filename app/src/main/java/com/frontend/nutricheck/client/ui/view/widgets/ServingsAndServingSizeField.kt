package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize

/**
 * A composable function that displays a servings field.
 *
 * @param value The number of servings to display.
 */
@Composable
fun ServingsField(
    value: Int
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography

    Box(
        modifier = Modifier
            .width(180.dp)
            .background(colors.surfaceVariant, RoundedCornerShape(8.dp))
            .border(1.dp, colors.outline, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$value",
                color = colors.onSurfaceVariant,
                style = styles.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

/**
 * A composable function that displays a serving size field.
 *
 * @param servingSize The serving size to display.
 */
@Composable
fun ServingSizeField(
    servingSize: ServingSize
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography

    Box(
        modifier = Modifier
            .width(180.dp)
            .background(colors.surfaceVariant, RoundedCornerShape(8.dp))
            .border(1.dp, colors.outline, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = servingSize.getDisplayName(),
                color = colors.onSurfaceVariant,
                style = styles.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}