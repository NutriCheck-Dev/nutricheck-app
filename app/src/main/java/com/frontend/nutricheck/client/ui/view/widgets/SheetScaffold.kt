package com.frontend.nutricheck.client.ui.view.widgets

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
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
    val configuration = LocalConfiguration.current
    val halfHeight = configuration.screenHeightDp.dp * 0.5f
    val maxHeight = configuration.screenHeightDp.dp * 0.75f

    var allowHide by remember { mutableStateOf(false) }

    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Hidden,
        skipHiddenState = false,
        confirmValueChange = { target ->
            !(target == SheetValue.Hidden && !allowHide)
        }
    )

    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState)


    LaunchedEffect(showSheet) {
        if (showSheet) {
            bottomSheetState.partialExpand()
        } else {
            allowHide = true
            bottomSheetState.hide()
            allowHide = false
        }
    }
    LaunchedEffect(bottomSheetState) {
        snapshotFlow { bottomSheetState.currentValue == SheetValue.Hidden }
            .distinctUntilChanged()
            .drop(1)
            .collect { hidden -> if (hidden) onSheetHidden() }
    }

    BottomSheetScaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        topBar = { topBar() },
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        sheetContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        sheetPeekHeight = halfHeight,
        sheetDragHandle = null,
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
            allowHide = true
            bottomSheetState.hide()
            allowHide = false
            onSheetHidden()
        }
    }
}