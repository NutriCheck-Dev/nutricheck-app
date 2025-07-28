package com.frontend.nutricheck.client.model.data_sources.data

import java.util.Date

data class Meal(
    val id: String,
    val name: String,
    val calories: Double,
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double,
    val date: Date,
    val dayTime: DayTime,
    val mealItems: List<MealItem>
)
