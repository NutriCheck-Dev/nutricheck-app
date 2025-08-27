package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.flags.SemanticsTags

/**
 * A composable function that displays a list of food components
 * It includes an optional add button for editing mode and allows for trailing content.
 *
 * @param modifier Modifier to be applied to the list.
 * @param foodComponents List of food components to display.
 * @param trailingContent Optional trailing content to display alongside each item.
 * @param editing Boolean flag to indicate if the list is in editing mode.
 * @param onAddButtonClick Callback function for when the add button is clicked.
 * @param onItemClick Callback function for when an item is clicked.
 */
@Composable
fun FoodComponentList(
    modifier: Modifier = Modifier,
    foodComponents: List<FoodComponent> = emptyList(),
    trailingContent: @Composable ((item: FoodComponent) -> Unit)? = null,
    editing: Boolean = false,
    onAddButtonClick: () -> Unit = {},
    onItemClick: (FoodComponent) -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if(editing) {
            OutlinedButton(
                onClick = { onAddButtonClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp)
                    .semantics { contentDescription = SemanticsTags.FOODCOMPONENT_LIST_ADD_BUTTON },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, colors.outline),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colors.onSurfaceVariant,
                    containerColor = colors.surfaceVariant
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.AddCircle,
                    contentDescription = stringResource(R.string.label_history_add)
                )
            }
        }
        foodComponents.forEach { item ->
            DishItemButton(
                modifier = Modifier.semantics { contentDescription = SemanticsTags.DISHITEM_PREFIX + item.name },
                foodComponent = item,
                trailingContent = { trailingContent?.invoke(item) },
                onClick = { onItemClick(item) })
        }
    }
}


/**
 * A composable function that displays a list of ingredients
 * It includes an optional add button for editing mode and allows for trailing content.
 *
 * @param modifier Modifier to be applied to the list.
 * @param ingredients List of ingredients to display.
 * @param trailingContent Optional trailing content to display alongside each item.
 * @param editing Boolean flag to indicate if the list is in editing mode.
 * @param onAddButtonClick Callback function for when the add button is clicked.
 * @param onItemClick Callback function for when an item is clicked.
 */
@Composable
fun IngredientList(
    modifier: Modifier = Modifier,
    ingredients: List<Ingredient> = emptyList(),
    trailingContent: @Composable ((item: Ingredient) -> Unit)? = null,
    editing: Boolean = false,
    onAddButtonClick: () -> Unit = {},
    onItemClick: (Ingredient) -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if(editing) {
            OutlinedButton(
                onClick = { onAddButtonClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, colors.outline),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colors.onSurfaceVariant,
                    containerColor = colors.surfaceVariant
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.AddCircle,
                    contentDescription = stringResource(R.string.label_history_add)
                )
            }
        }
        ingredients.forEach { item ->
            DishItemButton(
                modifier = Modifier.semantics { contentDescription = SemanticsTags.DISHITEM_PREFIX + item.foodProduct.name },
                ingredient = item,
                trailingContent = { trailingContent?.invoke(item) },
                onClick = { onItemClick(item) })
        }
    }
}
