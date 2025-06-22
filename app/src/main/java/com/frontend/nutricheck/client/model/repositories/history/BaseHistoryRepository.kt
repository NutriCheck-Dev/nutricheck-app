package com.frontend.nutricheck.client.model.repositories.history

import com.frontend.nutricheck.client.model.data_layer.FoodComponentId
import com.frontend.nutricheck.client.model.data_layer.History
import com.frontend.nutricheck.client.model.data_layer.Meal
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface BaseHistoryRepository {
    suspend fun getMealsForDay(date: Date): List<Meal>
    suspend fun addFoodToMeal(name: String, foodId: FoodComponentId)
    suspend fun removeFoodFromMeal(name: String, foodId: FoodComponentId)
    suspend fun observeHistory(): Flow<History>
}