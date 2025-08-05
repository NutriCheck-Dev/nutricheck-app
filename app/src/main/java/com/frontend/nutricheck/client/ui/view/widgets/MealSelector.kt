package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
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

    CenterAlignedTopAppBar(
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
                        text = dayTime?.toString() ?: "Select Meal",
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
                            text = { Text(dayTime.toString()) },
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