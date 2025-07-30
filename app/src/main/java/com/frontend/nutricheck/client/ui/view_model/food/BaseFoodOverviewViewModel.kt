package com.frontend.nutricheck.client.ui.view_model.food

import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import kotlinx.coroutines.Job

abstract class BaseFoodOverviewViewModel() :BaseViewModel() {

    abstract suspend fun onSaveChanges()
    abstract fun onBackClick(): Job
    abstract fun onServingsChanged(servings: Int)
    abstract fun onServingSizeChanged(servingSize: ServingSize)
}