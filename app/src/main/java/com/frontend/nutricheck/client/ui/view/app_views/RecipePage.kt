package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.frontend.nutricheck.client.ui.view.widgets.DishItemButton
import com.frontend.nutricheck.client.ui.view.widgets.DishItemList
import com.frontend.nutricheck.client.ui.view.widgets.FoodComponentSearchBar
import com.frontend.nutricheck.client.ui.view_model.navigation.NavigationActions

@Composable
fun RecipePage(
    modifier: Modifier = Modifier,
    actions: NavigationActions,
    //viewModel: RecipePageViewModel = hiltViewModel(),
    title: String = "Rezepte",
    localRecipes: @Composable () -> Unit,
    remoteRecipes: @Composable () -> Unit,
    onRecipeSelected: (String) -> Unit = {},
    onDetailsCick: (String) -> Unit = {},
    onAddRecipeClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }

    Column (
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        FoodComponentSearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        TabRowComponent(
            modifier = Modifier.fillMaxWidth(),
            tabs = listOf("Meine Rezepte", "Online Rezepte"),
            selectedTabIndex = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        Box(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        ) {
            val data = if (selectedTab == 0) localRecipes else remoteRecipes
            LazyColumn {
                item {
                    data()
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        onClick = onAddRecipeClick
                    ) {
                        Text("+ Rezept hinzufügen")
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

    }
}

@Preview
@Composable
fun RecipePagePreview() {
    val navController = rememberNavController()
    val previewActions = NavigationActions(navController)

    RecipePage(
        localRecipes = {
            DishItemList(
            title = "Meine Rezepte",
            list = listOf(
                {
                    DishItemButton(
                        title = "Gericht 1",
                        subtitle = "200 kcal, 100g",
                        onClick = {},
                        trailingContent = {
                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFFE0E0E0), shape = CircleShape)
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Hinzufügen",
                                    tint = Color.Black
                                )
                            }
                        })
                },
                {
                    DishItemButton(
                        title = "Gericht 2",
                        subtitle = "200 kcal, 100g",
                        onClick = {},
                        trailingContent = {
                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFFE0E0E0), shape = CircleShape)
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Hinzufügen",
                                    tint = Color.Black
                                )
                            }
                        })
                },
                {
                    DishItemButton(
                        title = "Gericht 3",
                        subtitle = "200 kcal, 100g",
                        onClick = {},
                        trailingContent = {
                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFFE0E0E0), shape = CircleShape)
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Hinzufügen",
                                    tint = Color.Black
                                )
                            }
                        })
                },
                {
                    DishItemButton(
                        title = "Gericht 4",
                        subtitle = "200 kcal, 100g",
                        onClick = {},
                        trailingContent = {
                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFFE0E0E0), shape = CircleShape)
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Hinzufügen",
                                    tint = Color.Black
                                )
                            }
                        })
                },
                {
                    DishItemButton(
                        title = "Gericht 5",
                        subtitle = "200 kcal, 100g",
                        onClick = {},
                        trailingContent = {
                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFFE0E0E0), shape = CircleShape)
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Hinzufügen",
                                    tint = Color.Black
                                )
                            }
                        })
                }
            )
        ) },
        remoteRecipes = { DishItemList(
            title = "Online Rezepte",
            list = listOf(
                {
                    DishItemButton(
                        title = "Gericht 1",
                        subtitle = "200 kcal, 100g",
                        onClick = {},
                        trailingContent = {
                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFFE0E0E0), shape = CircleShape)
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Hinzufügen",
                                    tint = Color.Black
                                )
                            }
                        })
                },
                {
                    DishItemButton(
                        title = "Gericht 2",
                        subtitle = "200 kcal, 100g",
                        onClick = {},
                        trailingContent = {
                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFFE0E0E0), shape = CircleShape)
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Hinzufügen",
                                    tint = Color.Black
                                )
                            }
                        })
                },
                {
                    DishItemButton(
                        title = "Gericht 3",
                        subtitle = "200 kcal, 100g",
                        onClick = {},
                        trailingContent = {
                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFFE0E0E0), shape = CircleShape)
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Hinzufügen",
                                    tint = Color.Black
                                )
                            }
                        })
                },
                {
                    DishItemButton(
                        title = "Gericht 4",
                        subtitle = "200 kcal, 100g",
                        onClick = {},
                        trailingContent = {
                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFFE0E0E0), shape = CircleShape)
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Hinzufügen",
                                    tint = Color.Black
                                )
                            }
                        })
                },
                {
                    DishItemButton(
                        title = "Gericht 5",
                        subtitle = "200 kcal, 100g",
                        onClick = {},
                        trailingContent = {
                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFFE0E0E0), shape = CircleShape)
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Hinzufügen",
                                    tint = Color.Black
                                )
                            }
                        })
                }
            )
        ) },
        actions = previewActions,
    )
}