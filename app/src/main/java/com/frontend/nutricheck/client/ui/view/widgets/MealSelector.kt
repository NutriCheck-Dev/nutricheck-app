package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.ui.theme.AppTheme

//This file represents the Header for the SearchPage
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealSelector(
    modifier: Modifier = Modifier,
    title: String = "Mahlzeit auswählen",
    mealOptions: List<String> = listOf("Frühstück", "Mittagessen", "Abendessen", "Snack"),
    onMealSelected: (String) -> Unit = {},
    onBack : () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    var expanded by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = {
            Button(
                onClick = { expanded = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = colors.onSurfaceVariant
                ),
                contentPadding = PaddingValues(vertical = 12.dp),
                elevation = null
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.width(7.dp))

                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Select Meal"
                )
            }
        },
        navigationIcon = { NavigateBackButton(onBack = onBack) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = colors.surfaceContainerHigh,
            titleContentColor = colors.onSurfaceVariant,
            navigationIconContentColor = colors.onSurfaceVariant
        )
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        mealOptions.forEach { meal ->
            DropdownMenuItem(
                text = { Text(meal) },
                onClick = {
                    onMealSelected(meal)
                    expanded = false
                          },
                )
            }
        }
}

@Preview
@Composable
fun MealSelectorPreview() {
    AppTheme(darkTheme = true) {
        MealSelector()
    }
}