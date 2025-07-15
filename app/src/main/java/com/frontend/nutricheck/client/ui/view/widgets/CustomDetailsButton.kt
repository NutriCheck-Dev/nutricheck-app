package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.frontend.nutricheck.client.model.data_sources.data.DropdownMenuOptions

@Composable
fun CustomDetailsButton(
    isOnDishItemButton: Boolean = false,
    isOnOwnedRecipe: Boolean = false,
    isOnPublicRecipe: Boolean = false,
    isOnFoodItem: Boolean = false,
    isOnIngredientButton: Boolean = false,
    onItemSelected: (DropdownMenuOptions) -> Unit = {},
    expanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit = { }
) {

    val optionsList = if (isOnOwnedRecipe) {
        DropdownMenuOptions.entries
            .minus(DropdownMenuOptions.SAVE)
            .sortedBy { it.name }
    } else if (isOnPublicRecipe) {
        DropdownMenuOptions.entries
            .minus(listOf(
                DropdownMenuOptions.EDIT,
                DropdownMenuOptions.DELETE,
                DropdownMenuOptions.UPLOAD))
            .sortedBy { it.name }
    } else if (isOnFoodItem) {
        listOf(DropdownMenuOptions.EDIT)
    } else if (isOnIngredientButton) {
        DropdownMenuOptions.entries
            .minus(listOf(
                DropdownMenuOptions.SAVE,
                DropdownMenuOptions.UPLOAD,
                DropdownMenuOptions.REPORT))
            .sortedBy { it.name }
    } else {emptyList()}


    Box{
        IconButton(
            modifier = Modifier.align(Alignment.Center),
            onClick = { onExpandedChange(true) }
        ) {
            if (isOnDishItemButton) {
                Icon(
                    imageVector = Icons.Filled.MoreHoriz,
                    contentDescription = "Details"
                )
            } else {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Details"
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            optionsList.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option.name) },
                    onClick = {
                        onExpandedChange(false)
                        onItemSelected(option)
                    }
                )
            }
        }
    }
}