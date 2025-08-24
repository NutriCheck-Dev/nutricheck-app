package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.ui.theme.AppTheme

//This file represents the Header for the SearchPage
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealSelector(
    dayTime: DayTime? = null,
    mealOptions: List<DayTime> = DayTime.entries.toList(),
    expanded: Boolean = false,
    onExpandedChange: () -> Unit = {},
    onMealSelected: (DayTime) -> Unit = {},
    onBack : () -> Unit = {},
    trailingContent: @Composable (() -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState()
    )

    CenterAlignedTopAppBar(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp)),
        windowInsets = WindowInsets(top = 0.dp),
        scrollBehavior = scrollBehavior,
        title = {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { onExpandedChange() },
                modifier = Modifier
                    .wrapContentWidth(
                        align = Alignment.CenterHorizontally,
                        unbounded = true
                    )
            ) {
                Row(
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryEditable)
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dayTime?.getDescription(context = LocalContext.current) ?: stringResource(R.string.label_select_meal),
                        style = styles.headlineMedium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                        Crossfade(targetState = expanded, label = "ArrowIcon") { onExpanded ->
                            Icon(
                                imageVector = if (!onExpanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropUp,
                                contentDescription = null,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                }

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { onExpandedChange() },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    mealOptions.forEach { dayTime ->
                        DropdownMenuItem(
                            text = { Text(dayTime.getDescription(context = LocalContext.current)) },
                            onClick = {
                                onMealSelected(dayTime)
                                onExpandedChange()
                            }
                        )
                    }
                }
            }
        },
        navigationIcon = { NavigateBackButton(onBack = onBack) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = colors.surfaceContainerHigh,
            titleContentColor = colors.onSurfaceVariant,
            navigationIconContentColor = colors.onSurfaceVariant
        ),
        actions = { trailingContent?.invoke() }
    )
}

@Preview
@Composable
fun MealSelectorPreview() {
    AppTheme {
        MealSelector()
    }
}