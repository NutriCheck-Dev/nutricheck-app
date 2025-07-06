package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Composable
fun CustomTabRow(
    modifier: Modifier = Modifier,
    options: List<String> = emptyList(),
    selectedOption: Int = 0,
    onSelect: (String) -> Unit = {}
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEachIndexed { index, label ->
            val color by animateColorAsState(
                if (index == selectedOption) Color.White
                else Color(0xFF707179),
                label = "segmentColor$index"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onSelect(label) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (index == selectedOption) FontWeight.Bold
                        else FontWeight.Normal
                    ),
                    color = color
                )
            }

            if (index < options.lastIndex) {
                VerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 4.dp),
                    color = Color(0xFF707179),
                    thickness = 1.dp
                )
            }
        }
    }
}

@Preview
@Composable
fun CustomTabRowPreview() {
    Box(modifier = Modifier.padding(16.dp)) {
        CustomTabRow(
            options = listOf("Option 1", "Option 2"),
            selectedOption = 1,
            onSelect = {}
        )
    }
}