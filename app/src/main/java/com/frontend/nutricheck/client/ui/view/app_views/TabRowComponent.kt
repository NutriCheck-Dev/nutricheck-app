package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TabRowComponent(
    modifier: Modifier = Modifier,
    tabs: List<String> = emptyList(),
    selectedTabIndex: Int = 0,
    onTabSelected: (Int) -> Unit,
    selectedColor: Color = Color.White,
    unselectedColor: Color = Color(0xFF707179)
) {
    var selectedTabIndex by remember { mutableIntStateOf(selectedTabIndex) }

    TabRow(
        modifier = modifier,
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.Transparent,
        contentColor = unselectedColor,
        indicator = {},
        divider = {}
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
            ) {
                Text(
                    text = title,
                    color = if (selectedTabIndex == index) selectedColor else unselectedColor
                )
            }
        }
    }
}

@Preview
@Composable
fun TabRowComponentPreview() {
    Box(modifier = Modifier.padding(16.dp)) {
        TabRowComponent(
            tabs = listOf("Tab 1", "Tab 2", "Tab 3"),
            selectedTabIndex = 0,
            onTabSelected = {}
        )
    }
}