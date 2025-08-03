package com.frontend.nutricheck.client.ui.view_model.add_components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComponentCoordinatorVM @Inject constructor(): ViewModel() {
    private val _added = MutableSharedFlow<Pair<Double, FoodComponent>>(replay = 0)
    val added: SharedFlow<Pair<Double, FoodComponent>> = _added

    fun addComponent(component: Pair<Double, FoodComponent>) {
        viewModelScope.launch { _added.emit(component) }
    }
}