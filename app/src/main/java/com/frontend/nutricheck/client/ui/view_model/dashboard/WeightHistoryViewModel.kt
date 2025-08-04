package com.frontend.nutricheck.client.ui.view_model.dashboard

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Weight
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
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

    fun displayWeightHistory() {
        viewModelScope.launch {
            val weightEntries: List<Weight> = userDataRepository.getWeightHistory()

            val weightList = weightEntries.map { it.value }
            val weightGoal = userDataRepository.getTargetWeight()

            _weightHistoryState.value = WeightHistoryState(
                weightData = weightList,
                weightGoal = weightGoal
            )
        }
    }
}