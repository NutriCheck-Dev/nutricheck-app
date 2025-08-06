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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.ui.view.widgets.CustomCloseButton
import com.frontend.nutricheck.client.ui.view.widgets.CustomPersistButton
import com.frontend.nutricheck.client.ui.view.widgets.FoodProductNutrientChartsWidget
import com.frontend.nutricheck.client.ui.view.widgets.ServingSizeDropdown
import com.frontend.nutricheck.client.ui.view.widgets.ServingsPicker
import com.frontend.nutricheck.client.ui.view.widgets.ViewsTopBar
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.food.FoodProductOverviewEvent
import com.frontend.nutricheck.client.ui.view_model.food.FoodProductOverviewMode
import com.frontend.nutricheck.client.ui.view_model.food.FoodProductOverviewViewModel
import com.frontend.nutricheck.client.ui.view_model.FoodSearchViewModel
import com.frontend.nutricheck.client.ui.view_model.SearchEvent

@Composable
fun FoodProductOverview(
    foodProductOverviewViewModel: FoodProductOverviewViewModel,
    foodSearchViewModel: FoodSearchViewModel? = null,
    onPersist: () -> Unit = { },
    onBack: () -> Unit = { }
) {
    val foodProductState by foodProductOverviewViewModel.foodProductViewState.collectAsState()
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    val onCancel = foodProductOverviewViewModel.onEvent(FoodProductOverviewEvent.GoBack)
    val uiState by foodProductOverviewViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            ViewsTopBar(
                navigationIcon = {
                    CustomCloseButton {
                        onCancel
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
                    if (foodProductState.mode is FoodProductOverviewMode.FromSearch)
                        CustomPersistButton { foodSearchViewModel!!.onEvent(
                            SearchEvent.AddFoodComponent(foodProductState.submitFoodProduct()))
                            onPersist()
                        } else null
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
                            text = "Serving Size:",
                            style = styles.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = colors.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        ServingSizeDropdown(
                            currentServingSize = foodProductState.parameters.servingSize,
                            onValueChange = {
                                foodProductOverviewViewModel.onEvent(
                                    FoodProductOverviewEvent.ServingSizeChanged(it)
                                )
                            }
                        )
                    }

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
                            value = foodProductState.parameters.servings,
                            range = 1..200,
                            onValueChange = {
                                foodProductOverviewViewModel.onEvent(
                                    FoodProductOverviewEvent.ServingsChanged(it)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}