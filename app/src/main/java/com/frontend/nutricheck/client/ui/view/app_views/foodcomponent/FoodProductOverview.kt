package com.frontend.nutricheck.client.ui.view.app_views.foodcomponent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.ui.view.widgets.CustomCloseButton
import com.frontend.nutricheck.client.ui.view.widgets.CustomPersistButton
import com.frontend.nutricheck.client.ui.view.widgets.FoodProductNutrientChartsWidget
import com.frontend.nutricheck.client.ui.view.widgets.ServingSizeDropdown
import com.frontend.nutricheck.client.ui.view.widgets.ServingSizeField
import com.frontend.nutricheck.client.ui.view.widgets.ServingsField
import com.frontend.nutricheck.client.ui.view.widgets.ServingsPicker
import com.frontend.nutricheck.client.ui.view.widgets.ViewsTopBar
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.FoodProductOverviewEvent
import com.frontend.nutricheck.client.ui.view_model.FoodProductOverviewMode
import com.frontend.nutricheck.client.ui.view_model.FoodProductOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeEditorEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.RecipeEditorViewModel
import com.frontend.nutricheck.client.ui.view_model.FoodSearchViewModel
import com.frontend.nutricheck.client.ui.view_model.SearchEvent

/**
 * Displays an overview of a food product, including its nutritional information,
 *
 * @param foodProductOverviewViewModel The ViewModel that manages the food product overview state.
 * @param foodSearchViewModel Optional ViewModel for food search functionality.
 * @param recipeEditorViewModel Optional ViewModel for recipe editing functionality.
 * @param onBack Callback function to handle back navigation.
 */
@Composable
fun FoodProductOverview(
    foodProductOverviewViewModel: FoodProductOverviewViewModel,
    foodSearchViewModel: FoodSearchViewModel? = null,
    recipeEditorViewModel: RecipeEditorViewModel? = null,
    onBack: () -> Unit = { }
) {
    val foodProductState by foodProductOverviewViewModel.foodProductViewState.collectAsState()
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    val uiState by foodProductOverviewViewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.testTag(stringResource(R.string.androidtest_tag_foodproduct_details)),
        topBar = {
            ViewsTopBar(
                navigationIcon = {
                    CustomCloseButton {
                        onBack()
                    }
                },
                title = {
                    Text(
                        text = foodProductState.parameters.foodName,
                        style = styles.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = colors.onSurfaceVariant
                    )
                },
                actions = {
                    if (foodProductState.parameters.editable) {
                        CustomPersistButton {
                            if (foodProductState.mode is FoodProductOverviewMode.FromSearch) {
                                foodSearchViewModel?.onEvent(SearchEvent.AddFoodComponent(foodProductState.submitFoodProduct()))
                                    ?: recipeEditorViewModel?.onEvent(
                                        RecipeEditorEvent.IngredientAdded(foodProductState.submitFoodProduct())
                                    )
                            } else {
                                foodProductOverviewViewModel.onEvent(FoodProductOverviewEvent.SaveAndAddClick)
                            }
                        }
                    }
                }

            )
        }
    ) { innerPadding ->

        when (uiState) {
            BaseViewModel.UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is BaseViewModel.UiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Error loading food product",
                        style = styles.bodyLarge,
                        color = colors.error
                    )
                }
            }
            BaseViewModel.UiState.Ready -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {

                    FoodProductNutrientChartsWidget(
                        actualCalories = foodProductState.parameters.calories,
                        actualCarbs = foodProductState.parameters.carbohydrates,
                        actualProtein = foodProductState.parameters.protein,
                        actualFat = foodProductState.parameters.fat
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        Text(
                            text = stringResource(R.string.foodproduct_servingsize_label),
                            style = styles.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = colors.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (foodProductState.parameters.editable) {
                            ServingSizeDropdown(
                                currentServingSize = foodProductState.parameters.servingSize,
                                onValueChange = {
                                    foodProductOverviewViewModel.onEvent(
                                        FoodProductOverviewEvent.ServingSizeChanged(it)
                                    )
                                }
                            )
                        } else {
                            ServingSizeField(
                                servingSize = foodProductState.parameters.servingSize
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        Text(
                            text = stringResource(R.string.foodcomponent_servings_label),
                            style = styles.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = colors.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (foodProductState.parameters.editable) {
                            ServingsPicker(
                                value = foodProductState.parameters.servings,
                                onValueChange = {
                                    foodProductOverviewViewModel.onEvent(
                                        FoodProductOverviewEvent.ServingsChanged(it)
                                    )
                                }
                            )
                        } else {
                            ServingsField(
                                value = foodProductState.parameters.servings
                            )
                        }
                    }
                }
            }
        }
    }
}