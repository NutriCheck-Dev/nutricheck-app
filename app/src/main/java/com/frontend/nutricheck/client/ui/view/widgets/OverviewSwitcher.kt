package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewSwitcher(
    modifier: Modifier = Modifier ,
    options: List<String> = emptyList(),
    selectedOption: String = "",
    onSelect: (String) -> Unit = {}
) {
    val selectedIndex = options.indexOf(selectedOption).coerceAtLeast(0)

    TabRow (
        modifier = modifier.fillMaxWidth(),
        selectedTabIndex = selectedIndex,
        containerColor = Color.Transparent,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedIndex])
                    .height(2.dp),
                color = Color.White
            )
        },
        divider = {}
    ) {
        options.forEachIndexed { index, title ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onSelect(title) },
                text = {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = if (index == selectedIndex) FontWeight.Bold
                        else FontWeight.Normal,
                        color = Color.White
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OverviewSwitcherPreview() {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        OverviewSwitcher(
            options = listOf("Option 1", "Option 2"),
            selectedOption = "Option 2",
            onSelect = {}
        )
    }
}