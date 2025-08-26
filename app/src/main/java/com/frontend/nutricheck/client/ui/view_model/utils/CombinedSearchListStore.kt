package com.frontend.nutricheck.client.ui.view_model.utils

import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * A store for managing the state of a combined search list.
 * This store holds a list of FoodComponent items and provides
 * a way to update the list.
 *
 * @property _state MutableStateFlow holding the current list of FoodComponent items.
 * @property state Immutable StateFlow exposing the current list of FoodComponent items.
 */
@ActivityRetainedScoped
class CombinedSearchListStore @Inject constructor() {
    private val _state = MutableStateFlow<List<FoodComponent>>(emptyList())
    val state: StateFlow<List<FoodComponent>> = _state
    fun update(list: List<FoodComponent>) { _state.value = list }
}