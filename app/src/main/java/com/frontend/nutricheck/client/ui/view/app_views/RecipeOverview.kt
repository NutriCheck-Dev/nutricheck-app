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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
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
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.ui.theme.AppTheme
import com.frontend.nutricheck.client.ui.view.widgets.DishItemList
import com.frontend.nutricheck.client.ui.view.widgets.NavigateBackButton
import com.frontend.nutricheck.client.ui.view.widgets.NutrientChartsWidget

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
    onBackClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            item {
                NutrientChartsWidget(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
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
                    list = ingredients,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
            }

            if (description.isNotBlank()) {
                item {
                    Text(
                        text = "Beschreibung",
                        style = styles.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        //color = colors.onSurfaceVariant
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
                                //color = colors.onSurfaceVariant
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

