package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.flags.DropdownMenuOptions
import com.frontend.nutricheck.client.model.data_sources.data.flags.SemanticsTags

/**
 * A custom details button that displays a dropdown menu with options based on the context.
 *
 * @param dishItemButton Indicates if the button is for a dish item.
 * @param ownedRecipe Indicates if the recipe is owned by the user.
 * @param publicRecipe Indicates if the recipe is public.
 * @param foodItem Indicates if the button is for a food item.
 * @param ingredientButton Indicates if the button is for an ingredient.
 * @param onOptionClick Callback function to handle option selection.
 * @param onDetailsClick Callback function to handle details click.
 * @param onDismissClick Callback function to handle dismiss action.
 * @param expanded Indicates if the dropdown menu is expanded.
 */
@Composable
fun CustomDetailsButton(
    modifier: Modifier = Modifier,
    dishItemButton: Boolean = false,
    ownedRecipe: Boolean = false,
    publicRecipe: Boolean = false,
    foodItem: Boolean = false,
    ingredientButton: Boolean = false,
    onOptionClick: (DropdownMenuOptions) -> Unit,
    onDetailsClick: () -> Unit = {},
    onDismissClick: () -> Unit = {},
    expanded: Boolean,
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    val optionsList = when {
        ownedRecipe -> {
            DropdownMenuOptions.entries
                .minus(
                    listOf(
                        DropdownMenuOptions.DOWNLOAD,
                        DropdownMenuOptions.REPORT).toSet()
                )
                .sortedBy { it.name }
        }
        publicRecipe -> {
            DropdownMenuOptions.entries
                .minus(
                    listOf(
                        DropdownMenuOptions.EDIT,
                        DropdownMenuOptions.DELETE,
                        DropdownMenuOptions.UPLOAD).toSet()
                )
                .sortedBy { it.name }
        }
        foodItem -> {
            listOf(DropdownMenuOptions.EDIT)
        }
        ingredientButton -> {
            DropdownMenuOptions.entries
                .minus(
                    listOf(
                        DropdownMenuOptions.DOWNLOAD,
                        DropdownMenuOptions.UPLOAD,
                        DropdownMenuOptions.REPORT).toSet()
                )
                .sortedBy { it.name }
        }
        else -> {emptyList()}
    }

    Box {
        IconButton(
            modifier = Modifier
                .align(Alignment.Center)
                .then(modifier),
            onClick = { onDetailsClick() }
        ) {
            if (dishItemButton) {
                Icon(
                    imageVector = Icons.Filled.MoreHoriz,
                    contentDescription = stringResource(R.string.details)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.details)
                )
            }
        }
        DropdownMenu(
            modifier = Modifier.semantics { contentDescription = SemanticsTags.DETAILS_MENU },
            expanded = expanded,
            onDismissRequest = { onDismissClick() }
        ) {
            optionsList.forEach { option ->
                DropdownMenuItem(
                    modifier = Modifier.semantics { contentDescription = SemanticsTags.DETAILS_MENU_OPTION_PREFIX + option.toString() },
                    text = {
                        Text(
                            text = option.toString(),
                            style = styles.titleSmall,
                            color = colors.onSurface
                        )},
                    leadingIcon = {
                        Icon(
                            imageVector = option.getIcon(),
                            contentDescription = option.toString()
                        )},
                    onClick = {
                        onDismissClick()
                        onOptionClick(option)
                    }
                )
                if (optionsList.last() != option) {
                    HorizontalDivider()
                }
            }
        }
    }
}