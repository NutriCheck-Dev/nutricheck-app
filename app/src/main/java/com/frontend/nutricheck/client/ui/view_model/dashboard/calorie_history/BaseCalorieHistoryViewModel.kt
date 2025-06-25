package com.frontend.nutricheck.client.ui.view_model.weight_history

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel


abstract class BaseCalorieHistoryViewModel : BaseViewModel() {

    abstract fun displayCalorieHistory(timePeriod: String)
}