package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.R

/**
 * A composable search bar for food components.
 *
 * @param modifier Modifier to be applied to the search bar.
 * @param query The current search query.
 * @param onQueryChange Callback invoked when the query changes.
 * @param onSearch Callback invoked when the search action is triggered.
 * @param placeholder Composable content to display as a placeholder in the search bar.
 * @param trailingIcon Optional trailing icon for the search bar.
 */
@Composable
fun FoodComponentSearchBar(
    modifier: Modifier = Modifier,
    query: String = "",
    onQueryChange: (String) -> Unit = {},
    onSearch: (String) -> Unit = {},
    placeholder: @Composable () -> Unit = { Text(stringResource(R.string.searchbar_placeholder_regular)) },
    trailingIcon: @Composable (() -> Unit)? = {
        Icon(Icons.Default.Search, contentDescription = stringResource(R.string.save)) },
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = modifier
                .fillMaxWidth(0.8f)
                .align(Alignment.Center)
                .defaultMinSize(minHeight = 56.dp),
            singleLine = true,
            placeholder = placeholder,
            trailingIcon = {
                IconButton(
                    onClick = {
                        onSearch(query)
                        keyboardController?.hide()
                    }
                ) {
                    trailingIcon?.invoke()
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch(query)
                    keyboardController?.hide()
                }
            ),
            shape = RoundedCornerShape(28.dp)
        )
    }
}