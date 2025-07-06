package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodComponentSearchBar(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    textFieldState: TextFieldState = remember { TextFieldState("") },
    onSearch: () -> Unit = {},
    onTextChange: (String) -> Unit = {},
    searchResults: List<String> = emptyList(),
    placeholder: @Composable () -> Unit = { Text(text = "Search", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold) },
    leadingIcon: @Composable (() -> Unit)? = { Icon(Icons.Default.Search, modifier = modifier.fillMaxSize().padding(10.dp), contentDescription = "Search", tint = Color.White) },
) {
    var text by remember { mutableStateOf(TextFieldValue()) }

    Row(
        modifier = Modifier
            .height(45.dp)
            .fillMaxWidth(0.8f)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(28.dp), ambientColor = Color.Gray, spotColor = Color.Gray)
            .background(Color.Transparent, RoundedCornerShape(28.dp))
            .clickable { onSearch() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            modifier = modifier
                .weight(1f)
                .padding(start = 16.dp, end = 8.dp)
                .align(Alignment.CenterVertically),
            value = text,
            onValueChange = {
                text = it
                onTextChange(it.text)
            },
            enabled = isEnabled,
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            ),
            decorationBox = { innerTextField ->
                if (text.text.isEmpty()) {
                    placeholder()
                }
                innerTextField()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = { onSearch()}),
            singleLine = true
        )
        Box(
            modifier = modifier
                .size(40.dp)
                .background(Color.Transparent, shape = CircleShape)
                .clickable {
                    if (text.text.isNotEmpty()) {
                        text = TextFieldValue("")
                        onTextChange("")
                    }
                }
        ) {
            if (text.text.isNotEmpty()) {
                Icon(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear Search",
                    tint = Color.White
                )
            } else { leadingIcon?.invoke() }
        }
    }
}

@Preview
@Composable
fun FoodComponentSearchBarPreview() {
    FoodComponentSearchBar(
        searchResults = listOf("Apple", "Banana", "Carrot")
    )
}