package com.frontend.nutricheck.client.ui.view_model.dashboard.weight_history

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Weight
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WeightHistoryState(
    //val selectedTimePeriod: String = "",
    val weightData: List<Weight> = emptyList(),
    val weightGoal : Double = 0.0,
)

@HiltViewModel
class WeightHistoryViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
) : BaseWeightHistoryViewModel() {

    private val _weightHistoryState = MutableStateFlow(WeightHistoryState())
    val weightHistoryState = _weightHistoryState.asStateFlow()

   override fun displayWeightHistory(timePeriod: String) {
       viewModelScope.launch {
           _weightHistoryState.value = WeightHistoryState(
               userDataRepository.getWeightHistory(),
               userDataRepository.getTargetWeight())
       }
   }
}