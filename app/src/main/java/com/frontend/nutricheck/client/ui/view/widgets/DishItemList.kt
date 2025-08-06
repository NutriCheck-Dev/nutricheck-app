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
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient

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
        foodComponents.forEach { item ->
            DishItemButton(
                foodComponent = item,
                trailingContent = { trailingContent?.invoke(item) },
                onClick = { onItemClick(item) })
        }
    }
}

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
                    contentDescription = "Hinzufügen"
                )
            }
        }
        ingredients.forEach { item ->
            DishItemButton(
                ingredient = item,
                trailingContent = { trailingContent?.invoke(item) },
                onClick = { onItemClick(item) })
        }
    }
}
