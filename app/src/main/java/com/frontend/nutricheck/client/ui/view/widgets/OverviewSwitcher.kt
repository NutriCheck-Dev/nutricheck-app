package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun OverviewSwitcher(
    modifier: Modifier = Modifier ,
    options: List<String> = emptyList(),
    selectedOption: String = "",
    onSelect: (String) -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        options.forEach { option ->
            Column (
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelect(option) }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = option,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (option == selectedOption) FontWeight.Bold
                    else FontWeight.Normal,
                    color = if (option == selectedOption) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(4.dp))

                Box(
                    Modifier
                        .height(2.dp)
                        .fillMaxWidth()
                        .background(
                            if (option == selectedOption)
                            MaterialTheme.colorScheme.primary
                            else
                            Color.Transparent
                        )
                )
            }
        }
    }
}

@Preview
@Composable
fun OverviewSwitcherPreview() {
    MaterialTheme {
        OverviewSwitcher(
            options = listOf("Option 1", "Option 2"),
            selectedOption = "Option 2",
            onSelect = {}
        )
    }
}