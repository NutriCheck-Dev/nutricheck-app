package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.ui.theme.AppTheme

@Composable
fun CustomTabRow(
    modifier: Modifier = Modifier,
    options: List<String> = emptyList(),
    selectedOption: Int = 0,
    onSelect: (Int) -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography

    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEachIndexed { index, label ->
            val selected = index == selectedOption
            val textColor by animateColorAsState(
                targetValue = if (selected) colors.onPrimaryContainer
                else colors.onSurfaceVariant,
            )

            val backgroundColor by animateColorAsState(
                targetValue = if (selected) colors.primaryContainer
                else Color.Transparent,
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(backgroundColor, RoundedCornerShape(4.dp))
                    .clickable { onSelect(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = styles.bodyMedium.copy(
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = textColor
                )
            }

            if (index < options.lastIndex) {
                VerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 4.dp),
                    color = colors.outline,
                    thickness = 1.dp
                )
            }
        }
    }
}

@Preview
@Composable
fun CustomTabRowPreview() {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            CustomTabRow(
                options = listOf("Option 1", "Option 2"),
                selectedOption = 1,
                onSelect = {}
            )
        }
    }
}