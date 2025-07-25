package com.frontend.nutricheck.client.ui.view_model.dashboard.weight_history

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel


abstract class BaseWeightHistoryViewModel : BaseViewModel() {

    abstract fun displayWeightHistory(timePeriod: String)
}