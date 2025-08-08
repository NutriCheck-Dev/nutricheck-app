package com.frontend.nutricheck.client.ui.view_model.dashboard

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Weight
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import com.frontend.nutricheck.client.ui.view.widgets.WeightRange
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

data class WeightHistoryState(
    val weightData: List<Weight> = emptyList(),
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

            val now = Date()
            val cutoffDate = when (range) {
                WeightRange.LAST_1_MONTH -> Date(now.time - 30L * 24 * 60 * 60 * 1000)
                WeightRange.LAST_6_MONTHS -> Date(now.time - 180L * 24 * 60 * 60 * 1000)
                WeightRange.LAST_12_MONTHS -> Date(now.time - 365L * 24 * 60 * 60 * 1000)
            }


            val filtered = weightEntries
                .filter { it.date.after(cutoffDate) || it.date == cutoffDate }
                .sortedBy { it.date }

            _weightHistoryState.value = WeightHistoryState(
                weightData = filtered,
                weightGoal = weightGoal
            )
        }
    }
}