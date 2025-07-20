package com.frontend.nutricheck.client.ui.view_model.add_components

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseAddAiMealViewModel : BaseViewModel() {
    abstract suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner)
    abstract fun takePhoto()
    abstract fun submitPhoto()
    abstract fun retakePhoto()
}