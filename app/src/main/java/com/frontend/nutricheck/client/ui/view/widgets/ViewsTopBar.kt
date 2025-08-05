package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
@Preview
@Composable
fun ViewsTopBar(
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
        color = containerColor,
        tonalElevation = tonalElevation,
        shape = shape,
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        CenterAlignedTopAppBar(
            navigationIcon = { navigationIcon?.invoke() },
            title = { title?.invoke() },
            actions = { actions?.invoke() },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = titleContentColor,
                navigationIconContentColor = navIconContentColor
            ),
            modifier = Modifier
                .fillMaxWidth(),
            windowInsets = WindowInsets(top = 0.dp)
        )
    }
}