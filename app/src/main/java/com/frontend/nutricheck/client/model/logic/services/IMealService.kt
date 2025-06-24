package com.frontend.nutricheck.client.model.services

import com.frontend.nutricheck.client.model.data_layer.Meal

interface IMealService {
    suspend fun addMeal(meal: Meal)
    suspend fun removeMeal(mealId: String)
    suspend fun getAllMeals(): List<Meal>
}