package com.frontend.nutricheck.client.model.data_sources.data

import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import java.util.Date

data class Meal(
    val id: String,
    val calories: Double,
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double,
    val date: Date,
    val dayTime: DayTime,
    val mealFoodItems: List<MealFoodItem>,
    val mealRecipeItem: List<MealRecipeItem>
)
