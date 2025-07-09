package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.ui.theme.AppTheme
import com.frontend.nutricheck.client.ui.view.widgets.CustomAddButton
import com.frontend.nutricheck.client.ui.view.widgets.DishItemButton
import com.frontend.nutricheck.client.ui.view.widgets.DishItemList
import com.frontend.nutricheck.client.ui.view.widgets.NavigateBackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeOverview(
    modifier: Modifier = Modifier,
    //actions: NavigationActions,
    //recipeOverviewViewModel: RecipeOverviewViewModel = hiltViewModel(),
    title: String = "Rezept",
    ingredients: List<@Composable () -> Unit> = emptyList(),
    description: String = "",
    onFoodClick: (String) -> Unit = {},
    onEditClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Surface(
                tonalElevation = 4.dp,
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = title,
                            style = styles.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = { NavigateBackButton() },
                    actions = {
                        IconButton(onClick = { onEditClick(title) }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Recipe",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = colors.surfaceContainerHigh,
                        titleContentColor = colors.onSurfaceVariant,
                        navigationIconContentColor = colors.onSurfaceVariant
                    )
                )
            }
        }
    ) { innerPadding ->

        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                //.verticalScroll(rememberScrollState())
                .background(colors.background)
                .padding(16.dp)

        ) {
            //item { Spacer(Modifier.height(31.dp)) }

            DishItemList(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.outline),
                title = "Zutaten",
                list = ingredients
            )


            //Spacer(modifier = Modifier.height(24.dp))
        }
        if (description.isNotBlank()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = description,
                style = styles.bodyMedium
            )
        }
    }
}

@Preview
@Composable
fun RecipeOverviewPreview() {
    AppTheme(darkTheme = true) {
        RecipeOverview(
            ingredients = listOf(
                {
                    DishItemButton(
                        title = "Gericht 1",
                        subtitle = "200 kcal, 100g",
                        onClick = {},
                        trailingContent = { CustomAddButton() })
                },
                {
                    DishItemButton(
                        title = "Gericht 2",
                        subtitle = "200 kcal, 100g",
                        onClick = {},
                        trailingContent = { CustomAddButton() })
                },
                {
                    DishItemButton(
                        title = "Gericht 3",
                        subtitle = "200 kcal, 100g",
                        onClick = {},
                        trailingContent = { CustomAddButton() })
                },
                {
                    DishItemButton(
                        title = "Gericht 4",
                        subtitle = "200 kcal, 100g",
                        onClick = {},
                        trailingContent = { CustomAddButton() })
                },
                {
                    DishItemButton(
                        title = "Gericht 5",
                        subtitle = "200 kcal, 100g",
                        onClick = {},
                        trailingContent = { CustomAddButton() })
                }
            ),
            description = "Eine leckere Rezeptbeschreibung"
        )
    }
}

