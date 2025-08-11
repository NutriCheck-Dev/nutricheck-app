package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CustomNumberPicker(
    modifier: Modifier = Modifier,
    list: List<String>,
    textStyle: TextStyle,
    state: LazyListState,
    flingBehavior: FlingBehavior,
    visibleCount: Int,
    halfCount: Int,
    onSelectedIndexChange: (Int) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val height = with(LocalDensity.current) {
        textStyle.fontSize.toDp() + 8.dp
    }
    val centeredIndex by remember {
        derivedStateOf { state.firstVisibleItemIndex + halfCount }
    }

    LaunchedEffect(centeredIndex) {
        val realIndex = (centeredIndex - halfCount).coerceIn(0, list.lastIndex)
        onSelectedIndexChange(realIndex)
    }

    Box(
        modifier = modifier
            .background(colors.surfaceVariant, RoundedCornerShape(8.dp))
            .border(1.dp, colors.outline, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = state,
            flingBehavior = flingBehavior,
            modifier = Modifier
                .padding(4.dp)
                .height(height * visibleCount)
                .fillMaxWidth()
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                .drawWithContent{
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            0f to Color.Transparent,
                            0.5f to colors.surfaceVariant,
                            1f to Color.Transparent
                        ),
                        blendMode = BlendMode.DstIn
                    )
                },
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(halfCount) {
                Spacer(Modifier
                    .fillMaxWidth()
                    .height(height)
                )
            }

            items(list) { value ->
                Text(
                    text = value,
                    style = textStyle,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height)
                        .wrapContentHeight(Alignment.CenterVertically)
                )
            }

            items(halfCount) {
                Spacer(Modifier
                    .fillMaxWidth()
                    .height(height)
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(.95f)
                .height(height + 4.dp)
                .background(colors.primary.copy(0.15f), RoundedCornerShape(8.dp))
                .border(1.dp, colors.primary, RoundedCornerShape(8.dp))
        )
    }
}