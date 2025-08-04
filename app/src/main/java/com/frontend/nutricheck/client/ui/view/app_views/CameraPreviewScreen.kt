package com.frontend.nutricheck.client.ui.view.app_views

import android.Manifest
import androidx.camera.compose.CameraXViewfinder
import androidx.compose.ui.zIndex
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.rememberAsyncImagePainter
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.ui.theme.extended
import com.frontend.nutricheck.client.ui.view.widgets.ExitButton
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.add_components.AddAiMealEvent
import com.frontend.nutricheck.client.ui.view_model.add_components.AddAiMealViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPreviewScreen(
    addAiMealViewModel: AddAiMealViewModel,
    onNavigateToFoodProductOverview: (String, String) -> Unit,
    onExit: () -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val surfaceRequest by addAiMealViewModel.surfaceRequest.collectAsState()
    val photoUri by addAiMealViewModel.photoUri.collectAsState()
    val uiState by addAiMealViewModel.uiState.collectAsState()

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var showCameraDialog = false
    if (photoUri == null) {
        LaunchedEffect(Unit) {
            addAiMealViewModel.bindToCamera(context, lifecycleOwner)
        }
    }
    LaunchedEffect(Unit) {
        when {
            cameraPermissionState.status.isGranted -> {
                addAiMealViewModel.bindToCamera(context, lifecycleOwner)
            }

            cameraPermissionState.status.shouldShowRationale -> {
                showCameraDialog = true
            }

            else -> {
                cameraPermissionState.launchPermissionRequest()
            }
        }
        addAiMealViewModel.events.collect { event ->
            when (event) {
                is AddAiMealEvent.ShowMealOverview -> {
                    onNavigateToFoodProductOverview(event.mealId, event.foodProductId)
                }
                else -> { /* other events */
                }
            }
        }
    }
    if (showCameraDialog) {
        AlertDialog(
            onDismissRequest = { showCameraDialog = false },
            title = { Text(stringResource(R.string.camera_permission_required)) },
            text = { Text(stringResource(R.string.camera_permission_explanation)) },
            confirmButton = {
                Button(onClick = {
                    cameraPermissionState.launchPermissionRequest()
                    showCameraDialog = false
                }) {
                    Text(stringResource(R.string.grant_permission))
                }
            },
            dismissButton = {
                Button(onClick = { showCameraDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
    if (uiState is BaseViewModel.UiState.Error) {
        ShowErrorMessage(
            error = (uiState as BaseViewModel.UiState.Error).message,
            onClick = {
                addAiMealViewModel.onEvent(AddAiMealEvent.ResetErrorState)
                addAiMealViewModel.onEvent(AddAiMealEvent.OnRetakePhoto)
            })
    }
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState is BaseViewModel.UiState.Loading) {
                CircularProgressIndicator()
            }
            ExitButton(
                onBack = { onExit() },
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp).zIndex(2f)
            )
            if (photoUri == null) {
                surfaceRequest?.let { request ->
                    CameraXViewfinder(
                        surfaceRequest = request,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                IconButton(
                    onClick = { addAiMealViewModel.onEvent(AddAiMealEvent.OnTakePhoto) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(24.dp)
                        .size(80.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RadioButtonChecked,
                        contentDescription = stringResource(R.string.take_photo),
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.extended.chartBlue.color
                    )
                }
            } else {
                Image(
                    painter = rememberAsyncImagePainter(photoUri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Button(onClick = { addAiMealViewModel.onEvent(AddAiMealEvent.OnRetakePhoto) }) {
                        Text(stringResource(R.string.retake_photo))
                    }
                    Button(onClick = { addAiMealViewModel.onEvent(AddAiMealEvent.OnSubmitPhoto) }) {
                        Text(stringResource(R.string.submit_photo))
                    }
                }
            }
        }
    }


@Composable
private fun ShowErrorMessage(
    error: String,
    onClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onClick() },
        title = { Text(error) },
        text = { Text(error) },
        confirmButton = {
            Button(onClick = { onClick() }) {
                Text(stringResource(R.string.label_ok))
            }
        }
    )
}
