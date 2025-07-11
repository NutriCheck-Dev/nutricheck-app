package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.frontend.nutricheck.client.ui.theme.AppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodComponentSearchBar(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState = remember { TextFieldState("") },
    onSearch: (String) -> Unit = {},
    searchResults: List<String> = emptyList(),
    placeholder: @Composable () -> Unit = { Text("Search") },
    leadingIcon: @Composable (() -> Unit)? = { Icon(Icons.Default.Search, contentDescription = "Search") },
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier
            .fillMaxWidth()
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.8f)
                .defaultMinSize(minHeight = 48.dp)
                .heightIn(max = 56.dp)
                .semantics { traversalIndex = 0f },
            shape = RoundedCornerShape(28.dp),
            inputField = {
                SearchBarDefaults.InputField(
                    query = textFieldState.text.toString(),
                    onQueryChange = { textFieldState.edit { replace(0, length, it) } },
                    onSearch = {
                        onSearch(textFieldState.text.toString())
                        expanded = false
                               },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    )
                },
                expanded = expanded,
                onExpandedChange = { expanded = it },
            ) {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    searchResults.forEach { result ->
                        ListItem(
                            headlineContent = { Text(result) },
                            modifier = Modifier
                                .clickable {
                                    textFieldState.edit { replace(0, length, result) }
                                    expanded = false
                                }
                                .fillMaxWidth()
                        )
                    }
                }
        }
    }
}

@Preview
@Composable
fun FoodComponentSearchBarPreview() {
    AppTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            FoodComponentSearchBar(
                searchResults = listOf("Apple", "Banana", "Carrot")
            )
        }
    }
}