package com.frontend.nutricheck.client.ui.view_model.weight

import com.frontend.nutricheck.client.model.data_layer.Weight
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow

data class WeightHistoryState(
    val weightHistory: List<Weight> = emptyList()
)

sealed interface WeightHistoryEvent {
    data class DisplayWeightHistory(val startDate: String, val endDate: String) : WeightHistoryEvent
    data class AddWeightEntry(val weightEntry: Weight) : WeightHistoryEvent
    data class DeleteWeightEntry(val weightId: String) : WeightHistoryEvent
    data class UpdateWeightEntry(val weightEntry: Weight) : WeightHistoryEvent
}

@HiltViewModel
class WeightHistoryRepository @Inject constructor(
    initialState: WeightHistoryState = WeightHistoryState(),
) : BaseWeightHistoryRepository() {
    private val _weightHistoryState = MutableStateFlow(WeightHistoryState())
    val weightHistoryState = _weightHistoryState.asStateFlow()

    val _events = MutableSharedFlow<WeightHistoryEvent>()
    val events: SharedFlow<WeightHistoryEvent> = _events.asSharedFlow()

    override suspend fun getWeightHistory(startDate: String, endDate: String): List<Weight> {
        TODO("Not yet implemented")
    }

    override suspend fun addWeightEntry(weightEntry: Weight): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWeightEntry(weightId: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateWeightEntry(weightEntry: Weight): Boolean {
        TODO("Not yet implemented")
    }
}