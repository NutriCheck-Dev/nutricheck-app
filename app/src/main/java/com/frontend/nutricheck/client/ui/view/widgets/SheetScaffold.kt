package com.frontend.nutricheck.client.ui.view.widgets

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetScaffold(
    modifier: Modifier,
    showSheet: Boolean,
    onSheetHidden: () -> Unit,
    topBar: @Composable (() -> Unit),
    sheetContent: @Composable ColumnScope.() -> Unit,
    content: @Composable (PaddingValues) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val peekHeight = screenHeight * 0.5f
    val maxHeight = screenHeight * 0.75f

    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Hidden,
        skipHiddenState = false
    )

    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState)


    LaunchedEffect(showSheet) {
        if (showSheet) {
            if (bottomSheetState.hasPartiallyExpandedState) {
                bottomSheetState.partialExpand()
            } else {
                bottomSheetState.expand()
            }
        } else {
            bottomSheetState.hide()
        }
    }
    LaunchedEffect(bottomSheetState) {
        snapshotFlow { bottomSheetState.currentValue }
            .distinctUntilChanged()
            .drop(1)
            .collect { if (it == SheetValue.Hidden) onSheetHidden() }
    }

    BottomSheetScaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                topBar()
            }
                 },
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        sheetContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        sheetPeekHeight = peekHeight,
        sheetDragHandle = { BottomSheetDefaults.DragHandle() },
        sheetSwipeEnabled = true,
        sheetContent = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 0.dp, max = maxHeight)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    sheetContent()
                }
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }

    BackHandler(enabled = bottomSheetState.currentValue != SheetValue.Hidden) {
        scope.launch {
            bottomSheetState.hide()
        }
    }
}