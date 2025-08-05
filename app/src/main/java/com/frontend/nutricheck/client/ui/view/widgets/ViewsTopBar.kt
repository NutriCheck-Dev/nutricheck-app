package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
@Preview
@Composable
fun ViewsTopBar(
    modifier: Modifier = Modifier,
    tonalElevation: Dp = 0.dp,
    shape: Shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
    navigationIcon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null,
) {
    val colors = MaterialTheme.colorScheme
    val containerColor = colors.surfaceContainerHigh
    val titleContentColor = colors.onSurfaceVariant
    val navIconContentColor = colors.onSurfaceVariant

    Surface(
        tonalElevation = tonalElevation,
        shape = shape
    ) {
        CenterAlignedTopAppBar(
            navigationIcon = { navigationIcon?.invoke() },
            title = { title?.invoke() },
            actions = { actions?.invoke() },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = containerColor,
                titleContentColor = titleContentColor,
                navigationIconContentColor = navIconContentColor
            ),
            expandedHeight = TopAppBarDefaults.MediumAppBarCollapsedHeight
        )
    }
}