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
import com.frontend.nutricheck.client.model.data_sources.data.flags.DropdownMenuOptions

@Composable
fun CustomDetailsButton(
    dishItemButton: Boolean = false,
    ownedRecipe: Boolean = false,
    publicRecipe: Boolean = false,
    foodItem: Boolean = false,
    ingredientButton: Boolean = false,
    onDownloadClick: () -> Unit = { },
    onDeleteClick: () -> Unit = { },
    onEditClick: () -> Unit = { },
    onUploadClick: () -> Unit = { },
    onReportClick: () -> Unit = { },
    expanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit = { }
) {
    val colors = MaterialTheme.colorScheme
    val styles = MaterialTheme.typography
    val optionsList = if (ownedRecipe) {
        DropdownMenuOptions.entries
            .minus(DropdownMenuOptions.DOWNLOAD)
            .sortedBy { it.name }
    } else if (publicRecipe) {
        DropdownMenuOptions.entries
            .minus(listOf(
                DropdownMenuOptions.EDIT,
                DropdownMenuOptions.DELETE,
                DropdownMenuOptions.UPLOAD))
            .sortedBy { it.name }
    } else if (foodItem) {
        listOf(DropdownMenuOptions.EDIT)
    } else if (ingredientButton) {
        DropdownMenuOptions.entries
            .minus(listOf(
                DropdownMenuOptions.DOWNLOAD,
                DropdownMenuOptions.UPLOAD,
                DropdownMenuOptions.REPORT))
            .sortedBy { it.name }
    } else {emptyList()}

    Box {
        IconButton(
            modifier = Modifier.align(Alignment.Center),
            onClick = { onExpandedChange(true) }
        ) {
            if (dishItemButton) {
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
                        onExpandedChange(false)
                        when (option) {
                            DropdownMenuOptions.DOWNLOAD -> onDownloadClick()
                            DropdownMenuOptions.DELETE -> onDeleteClick()
                            DropdownMenuOptions.EDIT -> onEditClick()
                            DropdownMenuOptions.UPLOAD -> onUploadClick()
                            DropdownMenuOptions.REPORT -> onReportClick()
                        }
                    }
                )
                if (optionsList.last() != option) {
                    HorizontalDivider()
                }
            }
        }
    }
}