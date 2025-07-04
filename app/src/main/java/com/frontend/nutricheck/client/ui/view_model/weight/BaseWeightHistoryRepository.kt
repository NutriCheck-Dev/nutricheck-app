package com.frontend.nutricheck.client.ui.view_model.weight

import com.frontend.nutricheck.client.model.data_sources.data.Weight
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseWeightHistoryRepository : BaseViewModel() {
    abstract suspend fun getWeightHistory(
        startDate: String,
        endDate: String
    ): List<Weight>

    abstract suspend fun addWeightEntry(weightEntry: Weight): Boolean

    abstract suspend fun deleteWeightEntry(weightId: String): Boolean

    abstract suspend fun updateWeightEntry(weightEntry: Weight): Boolean
}