package com.frontend.nutricheck.client.ui.view.app_views

import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.rememberAsyncImagePainter
import com.frontend.nutricheck.client.ui.view_model.add_components.AddAiMealViewModel

@Composable
fun CameraPreviewScreen(
    addAiMealViewModel: AddAiMealViewModel
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val surfaceRequest by addAiMealViewModel.surfaceRequest.collectAsState()
    val photoUri by addAiMealViewModel.photoUri.collectAsState()

    LaunchedEffect(Unit) {
        addAiMealViewModel.bindToCamera(context, lifecycleOwner)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (photoUri == null) {
            surfaceRequest?.let { request ->
                CameraXViewfinder(
                    surfaceRequest = request,
                    modifier = Modifier.fillMaxSize()
                )
            }
            IconButton(
                onClick = { addAiMealViewModel.takePhoto() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.RadioButtonChecked,
                    contentDescription = "Capture Photo"
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
                    Button(onClick = { addAiMealViewModel.retakePhoto() }) {
                        Text("Retake Photo")
                    }
                    Button(onClick = { addAiMealViewModel.submitPhoto() }) {
                        Text("Submit Photo")
                    }
                }
            }
        }
}