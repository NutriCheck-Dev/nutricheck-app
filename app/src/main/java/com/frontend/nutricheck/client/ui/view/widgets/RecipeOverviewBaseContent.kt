package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.RecipeVisibility

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeOverviewBaseContent(
    recipe: Recipe,
    onEdit: () -> Unit,
    onDelete: (Recipe) -> Unit,
    onUpload: (Recipe) -> Unit,
    onReport: (Recipe) -> Unit = {},
    onBack: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    var expanded by remember { mutableStateOf(false) }
    val foodComponents: Set<FoodComponent> =
        recipe.ingredients
            .map { it.foodComponent }
            .toSet()

    Scaffold(
        topBar = {
            ViewsTopBar(
                navigationIcon = { NavigateBackButton(onBack = onBack) },
                title = {
                        Text(
                            text = recipe.name,
                            style = styles.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = colors.onSurfaceVariant
                        )
                },
                actions = {
                        CustomDetailsButton(
                            expanded = expanded,
                            isOnDishItemButton = false,
                            isOnOwnedRecipe = recipe.visibility == RecipeVisibility.OWNER ,
                            isOnPublicRecipe = recipe.visibility == RecipeVisibility.PUBLIC,
                            onExpandedChange = { expanded = it }
                        )
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            item {
                NutrientChartsWidget(
                    modifier = Modifier.fillMaxWidth(),
                    recipe = recipe)
            }

            item {
                Text(
                    text = "Zutaten",
                    style = styles.titleMedium,
                    color = colors.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))

                DishItemList(
                    isEditing = false,
                    foodComponents = foodComponents,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            item {
                Text(
                    text = "Beschreibung",
                    style = styles.titleMedium,
                )
                Spacer(modifier = Modifier.height(10.dp))
                if (recipe.description.isNotBlank()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, colors.outline)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = recipe.description,
                                style = styles.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}