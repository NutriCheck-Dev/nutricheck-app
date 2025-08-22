package com.frontend.nutricheck.client.ui.view.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

/**
 * A custom number picker component that allows users to select a value from a list of strings.
 *
 * @param modifier Modifier to customize the appearance and behavior of the picker.
 * @param list The list of strings to display in the picker.
 * @param textStyle The style to apply to the text displayed in the picker.
 * @param state The state of the lazy list used for displaying the items.
 * @param flingBehavior The fling behavior to apply to the lazy list.
 * @param visibleCount The number of items to display at once in the picker.
 * @param onSelectedIndexChange Callback function to be invoked when the selected index changes.
 * @param selectedInitialIndex The initial index to scroll to when the picker is first displayed.
 */
@Composable
fun CustomNumberPicker(
    modifier: Modifier = Modifier,
    list: List<String>,
    textStyle: TextStyle,
    state: LazyListState,
    flingBehavior: FlingBehavior,
    visibleCount: Int,
    onSelectedIndexChange: (Int) -> Unit,
    selectedInitialIndex: Int = 0
) {
    val colors = MaterialTheme.colorScheme

    val rowHeight = with(LocalDensity.current) { (textStyle.fontSize.toDp() + 8.dp).roundToPx() }
    val viewPortHeight = with(LocalDensity.current) { (textStyle.fontSize.toDp() + 8.dp) * visibleCount }
    val centerPadding = with(LocalDensity.current) { (viewPortHeight / 2f - (rowHeight / 2f).toDp()) }

    var didInitialScroll by remember { mutableStateOf(false) }
    LaunchedEffect(selectedInitialIndex, didInitialScroll) {
        if (!didInitialScroll) {
            snapshotFlow { state.layoutInfo.viewportEndOffset - state.layoutInfo.viewportStartOffset }
                .first { it > 0 }
            state.scrollToItem(selectedInitialIndex)
            didInitialScroll = true
        }
    }

    LaunchedEffect(state) {
        snapshotFlow {
            val info = state.layoutInfo
            if (info.visibleItemsInfo.isEmpty()) -1 else {
                val viewportCenter = (info.viewportStartOffset + info.viewportEndOffset) / 2
                info.visibleItemsInfo.minByOrNull { itemInfo ->
                    val center = itemInfo.offset + itemInfo.size / 2
                    kotlin.math.abs(center - viewportCenter)
                }?.index ?: -1
            }
        }
            .filter { it >= 0 }
            .distinctUntilChanged()
            .collect { onSelectedIndexChange(it) }
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
                .height(viewPortHeight)
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = centerPadding)
        ) {
            items(list.size) { index ->
                Text(
                    text = list[index],
                    style = textStyle,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(with(LocalDensity.current) { rowHeight.toDp() })
                        .wrapContentHeight(Alignment.CenterVertically)
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(.95f)
                .height(with(LocalDensity.current) { rowHeight.toDp() } + 8.dp)
                .background(colors.primary.copy(0.15f), RoundedCornerShape(8.dp))
                .border(1.dp, colors.primary, RoundedCornerShape(8.dp))
        )
    }
}