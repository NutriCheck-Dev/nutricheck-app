package com.frontend.nutricheck.client.ui.view_model.snackbar

import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider

/**
 * A composable function that displays a Snackbar at the bottom of the screen.
 *
 * @param snackbarHostState The state of the Snackbar host.
 * @param modifier Optional [Modifier] to apply to the Snackbar.
 */
@Composable
fun AppSnackbarHost(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value != SwipeToDismissBoxValue.Settled) {
                snackbarHostState.currentSnackbarData?.dismiss()
                true
            } else {
                false
            }
        }
    )

    LaunchedEffect(snackbarHostState.currentSnackbarData) {
        snackbarHostState.currentSnackbarData?.let {
            dismissState.reset()
        }
    }

    snackbarHostState.currentSnackbarData
        ?.takeIf { it.visuals.message.isNotEmpty() }
        ?.let { data ->
            Dialog(
                onDismissRequest = { data.dismiss() },
                properties = DialogProperties(
                    dismissOnClickOutside = true,
                    dismissOnBackPress = true,
                    usePlatformDefaultWidth = false
                )
            ) {
                (LocalView.current.parent as DialogWindowProvider).window.apply {
                    setGravity(Gravity.BOTTOM)
                    clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    attributes = attributes.apply {
                        width = WindowManager.LayoutParams.MATCH_PARENT
                    }
                }

                val density = LocalDensity.current
                val navigationBarPadding = with(density) {
                    WindowInsets.navigationBars.getBottom(this).toDp()
                }

                SwipeToDismissBox(
                    modifier = modifier.padding(bottom = navigationBarPadding),
                    state = dismissState,
                    backgroundContent = {}
                ) {
                    SnackbarHost(hostState = snackbarHostState) {
                        Snackbar(
                            snackbarData = it,
                            actionOnNewLine = true,
                            containerColor = colors.onSurface,
                            contentColor = colors.surfaceVariant,
                            actionColor = colors.primary
                        )
                    }
                }
            }
        }
}