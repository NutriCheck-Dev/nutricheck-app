package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize

@Composable
fun ServingSizeDropdown(
    modifier: Modifier = Modifier,
    options: List<ServingSize> = ServingSize.entries.toList(),
    currentServingSize: ServingSize,
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    onSelect: (ServingSize) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography

    Box(
        modifier = modifier
            .width(180.dp)
            .background(colors.surfaceVariant, RoundedCornerShape(8.dp))
            .border(1.dp, colors.outline, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            tonalElevation = 2.dp,
            shape = RoundedCornerShape(8.dp),
            color =  colors.surfaceVariant,
            modifier = Modifier
                .wrapContentSize()
                .clickable { onExpandedChange() }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = currentServingSize.getDisplayName(),
                    style = styles.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Icon",
                    tint = colors.onSurfaceVariant
                )
            }
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onExpandedChange() },
        modifier = Modifier
            .wrapContentSize()
            .background(colors.onSurface, RoundedCornerShape(8.dp))
    ) {
        options.forEach { size ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = size.getDisplayName(),
                        style = styles.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.onSurface
                    ) },
                onClick = {
                    onSelect(size)
                    onExpandedChange()
                }
            )
        }
    }
}