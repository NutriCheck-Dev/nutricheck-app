package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
    tabs: List<String>,
    contentScreens: List<@Composable () -> Unit> = emptyList(),
    containerColor: Color = Color.Transparent,
    notSelectedColor: Color = Color(0xFF71727A),
    selectedColor: Color = Color.White,
    indicatorColor: Color = Color.Transparent
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = containerColor,
            contentColor = notSelectedColor,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = indicatorColor
                )
            }
        ) {
            tabs.forEachIndexed { index, tabTitle ->
                Tab(
                    modifier = Modifier.padding(all = 16.dp),
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index}
                ) {
                    Text(text = tabTitle, color = if (selectedTabIndex == index) selectedColor else notSelectedColor)
                }
            }
        }

        contentScreens.getOrNull(selectedTabIndex)?.invoke()
    }
}

@Preview
@Composable
fun TabRowComponentPreview() {
    Box(modifier = Modifier.padding(16.dp)) {
        TabRowComponent(
            tabs = listOf("Meine Rezepte", "Online Rezepte")
        )
    }
}