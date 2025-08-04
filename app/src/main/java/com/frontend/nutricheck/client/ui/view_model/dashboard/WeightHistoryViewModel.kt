package com.frontend.nutricheck.client.ui.view_model.dashboard

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import com.frontend.nutricheck.client.ui.view.widgets.WeightRange
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WeightHistoryState(
    val weightData: List<Double> = emptyList(),
    val weightGoal : Double = 0.0,
)

@HiltViewModel
class WeightHistoryViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
) : BaseViewModel() {

    private val _weightHistoryState = MutableStateFlow(WeightHistoryState())
    val weightHistoryState = _weightHistoryState.asStateFlow()


    fun displayWeightHistory(range: WeightRange) {
        viewModelScope.launch {
            val weightEntries = userDataRepository.getWeightHistory()
            val weightGoal = userDataRepository.getTargetWeight()

            val fullList = weightEntries.map { it.value }

            val targetSize = when (range) {
                WeightRange.LAST_1_MONTH -> 30
                WeightRange.LAST_6_MONTHS -> 180
                WeightRange.LAST_12_MONTHS -> 365
            }

            val padded = fullList.takeLast(targetSize)
                .padStartWithDefault(targetSize, fullList.firstOrNull() ?: 0.0)

            _weightHistoryState.value = WeightHistoryState(
                weightData = padded,
                weightGoal = weightGoal
            )
        }
    }
    private fun <T> List<T>.padStartWithDefault(size: Int, default: T): List<T> {
        return List((size - this.size).coerceAtLeast(0)) { default } + this
    }
}