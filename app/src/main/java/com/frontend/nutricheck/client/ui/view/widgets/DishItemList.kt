package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.ui.theme.AppTheme

@Composable
fun DishItemList(
    modifier: Modifier = Modifier,
    list: List<FoodComponent> = emptyList(),
    trailingContent: @Composable (() -> Unit)? = null ,
    isEditing: Boolean = false,
    onAddButtonClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        list.forEach { item ->
            DishItemButton(foodComponent = item, trailingContent = trailingContent, onClick = onClick)
        }

        if(isEditing) {
            IconButton(
                onClick = onAddButtonClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp)
                    .border(
                        BorderStroke(2.dp, colors.onSurface),
                        shape = RoundedCornerShape(16.dp)
                    ),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = colors.onSurface,
                    containerColor = Color.Transparent
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.AddCircle,
                    contentDescription = "Hinzufügen"
                )
            }
        }
    }
}

@Preview
@Composable
fun DishItemListPreview() {
    AppTheme(darkTheme = true) {
        DishItemList(
            isEditing = true,
            list = listOf(
                Recipe(),
                Recipe(),
                Recipe(),
                Recipe()
            )
        )
    }
}