package com.frontend.nutricheck.client.model.data_layer

import java.util.Date

data class History(val date: Date, val meals: List<Meal>) {
    private var totalCalories: Int? = 0

    fun updateTotalCalories() {
        totalCalories = meals.sumOf { it.energyInKcal }
    }
}
