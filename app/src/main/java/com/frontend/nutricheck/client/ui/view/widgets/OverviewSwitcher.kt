package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
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

    val textMeasurer = rememberTextMeasurer()

    val textLayoutResult = remember(selectedOption) {
        textMeasurer.measure(
            text = AnnotatedString(options.getOrNull(selectedIndex) ?: ""),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }

    val indicatorWidth = with(LocalDensity.current) {
        textLayoutResult.size.width.toDp()
    }

    TabRow (
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        selectedTabIndex = selectedIndex,
        containerColor = Color.Transparent,
        indicator = { tabPositions ->
            val current = tabPositions[selectedIndex]
            Box(
                Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.BottomStart)
                    .offset(x = current.left)
                    .width(indicatorWidth)
                    .height(2.dp)
                    .background(Color.White)
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