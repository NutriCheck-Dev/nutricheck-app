package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.flags.SemanticsTags

/**
 * A composable function that displays a switcher for selecting between different overview options.
 *
 * @param options List of strings representing the overview options.
 * @param selectedOption The currently selected overview option.
 * @param onSelect Callback function to be invoked when an option is selected.
 * @param tonalElevation The elevation of the switcher, used for shadow effects.
 * @param shape The shape of the switcher, defaulting to a rounded corner shape.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewSwitcher(
    options: List<String> = emptyList(),
    selectedOption: String = "",
    onSelect: (String) -> Unit = {},
    tonalElevation: Dp = 0.dp,
    shape: Shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState()
    )

    Surface(
        color = colors.surfaceContainerHigh,
        tonalElevation = tonalElevation,
        shape = shape,
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        CenterAlignedTopAppBar(
            scrollBehavior = scrollBehavior,
            windowInsets = WindowInsets(top = 0.dp),
            title = {
                TabRow(
                    selectedTabIndex = selectedIndex,
                    containerColor = Color.Transparent,
                    contentColor = colors.onSurfaceVariant,
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
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .semantics { SemanticsTags.OVERVIEW_SWITCHER_TAB_PREFIX + title },
                        selected = index == selectedIndex,
                        onClick = { onSelect(title) },
                        selectedContentColor = colors.onSurfaceVariant,
                        unselectedContentColor = colors.onPrimaryContainer,
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
            }},
            navigationIcon = {},
            actions = {},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
        )
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