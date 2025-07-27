package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewSwitcher(
    modifier: Modifier = Modifier ,
    options: List<String> = emptyList(),
    selectedOption: String = "",
    onSelect: (String) -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    val selectedIndex = options.indexOf(selectedOption).coerceAtLeast(0)
    val density = LocalDensity.current
    val tabWidths = remember {
        val tabWidthStateList = mutableStateListOf<Dp>()
        repeat(options.size) {
            tabWidthStateList.add(0.dp)
        }
        tabWidthStateList
    }


    TabRow (
        modifier = modifier
            .fillMaxWidth()
            .height(TopAppBarDefaults.TopAppBarExpandedHeight),
        selectedTabIndex = selectedIndex,
        containerColor = colors.surfaceContainerHigh,
        contentColor = colors.onPrimaryContainer,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.customTabIndicatorOffset(
                    currentTabPosition = tabPositions[selectedIndex],
                    tabWidth = tabWidths[selectedIndex]
                ),
                height = 3.dp,
                color = colors.onPrimaryContainer
            )
        },
        divider = {}
    ) {
        options.forEachIndexed { index, title ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onSelect(title) },
                selectedContentColor = colors.onPrimaryContainer,
                unselectedContentColor = colors.onSurfaceVariant,
                text = {
                    Text(
                        title,
                        style = styles.titleLarge,
                        fontWeight = if (index == selectedIndex) FontWeight.Bold
                        else FontWeight.Normal,
                        onTextLayout = { textLayoutResult ->
                            tabWidths[index] = with(density) {
                                textLayoutResult.size.width.toDp()
                            }
                        }
                    )
                }
            )
        }
    }
}

fun Modifier.customTabIndicatorOffset(
    currentTabPosition: TabPosition,
    tabWidth: Dp
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "customTabIndicatorOffset"
        value = currentTabPosition
    }
) {
    val currentTabWidth by animateDpAsState(
        targetValue = tabWidth,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
    )
    val indicatorOffset by animateDpAsState(
        targetValue = ((currentTabPosition.left + currentTabPosition.right - tabWidth) / 2),
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
    )
    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = indicatorOffset)
        .width(currentTabWidth)
}

@Preview(showBackground = true)
@Composable
fun OverviewSwitcherPreview() {
    AppTheme() {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            OverviewSwitcher(
                options = listOf("Option 1", "Option 2"),
                selectedOption = "Option 2",
                onSelect = {}
            )
        }
    }
}