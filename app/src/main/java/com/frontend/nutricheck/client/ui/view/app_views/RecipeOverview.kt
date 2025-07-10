package com.frontend.nutricheck.client.ui.view.app_views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.ui.theme.AppTheme
import com.frontend.nutricheck.client.ui.view.widgets.DishItemList
import com.frontend.nutricheck.client.ui.view.widgets.NavigateBackButton
import com.frontend.nutricheck.client.ui.view.widgets.NutrientChartsWidget
import com.frontend.nutricheck.client.ui.view.widgets.RecipeOverviewTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeOverview(
    modifier: Modifier = Modifier,
    //actions: NavigationActions,
    //recipeOverviewViewModel: RecipeOverviewViewModel = hiltViewModel(),
    title: String = "Rezept",
    ingredients: List<FoodComponent> = emptyList(),
    description: String = "",
    onFoodClick: (String) -> Unit = {},
    onEditClick: (String) -> Unit = {},
    onSave: (String, String) -> Unit = { _, _ -> },
    onBackClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    var isEditing by remember { mutableStateOf(false) }
    var titleText by remember { mutableStateOf(title) }
    var descriptionText by remember { mutableStateOf(description) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
        topBar = {
            RecipeOverviewTopBar(
                title = titleText,
                isEditing = isEditing,
                onTitleChange = { titleText = it },
                onEditToggle = {
                    if (isEditing) onSave(titleText, descriptionText)
                    isEditing = !isEditing
                },
                onBack = onBackClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            item {
                NutrientChartsWidget(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            item {
                Text(
                    text = "Zutaten",
                    style = styles.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    //color = colors.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))

                DishItemList(
                    isEditing = isEditing,
                    list = ingredients,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
            }

            if (isEditing) {
                item {
                    TextField(
                        value = descriptionText,
                        onValueChange = { descriptionText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            } else if (descriptionText.isNotBlank()) {
                item {
                    Text(
                        text = "Beschreibung",
                        style = styles.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, colors.outline)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = description,
                                style = styles.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun RecipeOverviewPreview() {
    AppTheme(darkTheme = true) {
        RecipeOverview(
            ingredients = listOf(
                Recipe(),
                Recipe(),
                Recipe(),
                Recipe(),
                ),
            description = "Dies ist eine Beispielbeschreibung für ein Rezept. Hier können Details zum Zubereitungsvorgang, den Zutaten und anderen wichtigen Informationen stehen.",
        )
    }
}

