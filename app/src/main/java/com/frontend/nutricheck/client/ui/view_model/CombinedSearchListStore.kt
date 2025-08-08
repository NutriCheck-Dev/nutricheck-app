package com.frontend.nutricheck.client.ui.view_model

import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@ActivityRetainedScoped
class CombinedSearchListStore @Inject constructor() {
    private val _state = MutableStateFlow<List<FoodComponent>>(emptyList())
    val state: StateFlow<List<FoodComponent>> = _state
    fun update(list: List<FoodComponent>) { _state.value = list }
}