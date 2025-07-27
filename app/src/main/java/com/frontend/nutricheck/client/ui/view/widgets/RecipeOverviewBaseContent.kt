package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.RecipeVisibility
import com.frontend.nutricheck.client.ui.view.dialogs.ReportRecipeDialog
import com.frontend.nutricheck.client.ui.view_model.recipe.report.ReportRecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeOverviewBaseContent(
    reportRecipeViewModel: ReportRecipeViewModel,
    recipe: Recipe,
    ingredients: List<FoodProduct>,
    onItemClick: (FoodComponent) -> Unit = {},
    onDownLoad: (Recipe) -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: (Recipe) -> Unit = {},
    onUpload: (Recipe) -> Unit = {},
    onSendReport: (Recipe) -> Unit = {},
    showReportDialog: Boolean = false,
    onReportClick: () -> Unit = {},
    onDismiss: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    var expanded by remember { mutableStateOf(false) }
    var count by remember { mutableIntStateOf(1) }

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
                            dishItemButton = false,
                            ownedRecipe = recipe.visibility == RecipeVisibility.OWNER ,
                            publicRecipe = recipe.visibility == RecipeVisibility.PUBLIC,
                            onExpandedChange = { expanded = it },
                            onDownloadClick = { onDownLoad },
                            onEditClick = { onEdit },
                            onDeleteClick = { onDelete },
                            onUploadClick = { onUpload },
                            onReportClick = { onReportClick }
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

            //TODO: Add actual values for total calories, carbs, protein, and fat
            item {
                RecipeNutrientChartsWidget(
                    modifier = Modifier.fillMaxWidth(),
                    recipe = recipe,
                    totalCalories = 0.0,
                    totalCarbs = 0.0,
                    totalProtein =  0.0,
                    totalFat = 0.0
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Text(
                        text = "Servings:",
                        style = styles.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    ServingsPicker(
                        value = count,
                        range = 1..200,
                        onValueChange = { count = it }
                    )
                }
            }

            item {
                Text(
                    text = "Zutaten",
                    style = styles.titleMedium,
                    color = colors.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))

                DishItemList(
                    editing = false,
                    foodComponents = ingredients,
                    onItemClick = { foodProduct ->
                        onItemClick(foodProduct)
                                  },
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
                if (recipe.instructions.isNotBlank()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, colors.outline)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = recipe.instructions,
                                style = styles.bodyMedium
                            )
                        }
                    }
                }
            }

            if (showReportDialog) {
                item {
                    ReportRecipeDialog(
                        reportRecipeViewModel = reportRecipeViewModel,
                        title = "Report",
                        confirmText = "Send",
                        cancelText = "Cancel",
                        onConfirm = { onSendReport },
                        onDismiss = { onDismiss },
                        reportText = "Please provide a reason for reporting this recipe."
                        //TODO: Implement onValueChange for report text
                    )
                }
            }
        }
    }
}