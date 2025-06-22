package com.frontend.nutricheck.client.model.services

import com.frontend.nutricheck.client.model.data_layer.Meal
import com.frontend.nutricheck.client.model.data_layer.FoodComponentId

interface IMealService {
    suspend fun addMeal(meal: Meal)
    suspend fun removeMeal(mealId: FoodComponentId)
    suspend fun getAllMeals(): List<Meal>
}