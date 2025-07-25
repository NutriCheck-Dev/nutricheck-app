package com.frontend.nutricheck.client.ui.view.app_views.foodcomponent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.frontend.nutricheck.client.ui.theme.AppTheme
import com.frontend.nutricheck.client.ui.view.widgets.CustomCloseButton
import com.frontend.nutricheck.client.ui.view.widgets.CustomPersistButton
import com.frontend.nutricheck.client.ui.view.widgets.FoodProductNutrientChartsWidget
import com.frontend.nutricheck.client.ui.view.widgets.ServingSizeDropdown
import com.frontend.nutricheck.client.ui.view.widgets.ServingsPicker
import com.frontend.nutricheck.client.ui.view.widgets.ViewsTopBar
import com.frontend.nutricheck.client.ui.view_model.food.FoodProductOverviewEvent
import com.frontend.nutricheck.client.ui.view_model.food.FoodProductOverviewViewModel

@Composable
fun FoodProductOverview(
    foodProductOverviewViewModel: FoodProductOverviewViewModel = hiltViewModel()
) {
    val foodProductState by foodProductOverviewViewModel.foodProductOverviewState.collectAsState()
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    val onPersist = foodProductOverviewViewModel.onEvent(FoodProductOverviewEvent.SaveAndAddClick)
    val onCancel = foodProductOverviewViewModel.onEvent(FoodProductOverviewEvent.GoBack)
    val actions = if (foodProductState.isFromIngredient) CustomPersistButton { onPersist } else null

    Scaffold(
        topBar = {
            ViewsTopBar(
                navigationIcon = { CustomCloseButton(onClick = { onCancel }) },
                title = {
                    Text(
                        text = foodProductState.foodName,
                        style = styles.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = colors.onSurfaceVariant
                    )
                },
                actions = { actions }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            FoodProductNutrientChartsWidget(
                foodProduct = foodProductState.foodProduct,
                modifier = Modifier.wrapContentHeight()
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
                    currentServingSize = foodProductState.servingSize,
                    expanded = foodProductState.servingSizeDropDownExpanded,
                    onExpandedChange = { foodProductOverviewViewModel.onEvent(
                        FoodProductOverviewEvent.ServingSizeDropDownClick
                    ) },
                    onSelect = { foodProductOverviewViewModel.onEvent(
                        FoodProductOverviewEvent.ServingSizeChanged(it)
                    )}
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
                    value = foodProductState.servings,
                    range = 1..200,
                    onValueChange = { foodProductOverviewViewModel.onEvent(
                        FoodProductOverviewEvent.ServingsChanged(it)
                    ) }
                )
            }
        }
    }
}

@Preview
@Composable
fun FoodProductOverviewPreview() {
    AppTheme(darkTheme = true) {
        FoodProductOverview()
    }
}