package com.frontend.nutricheck.client.model.repositories.history

import com.frontend.nutricheck.client.model.data_layer.FoodComponentId
import com.frontend.nutricheck.client.model.data_layer.History
import com.frontend.nutricheck.client.model.data_layer.Meal
import kotlinx.coroutines.flow.Flow
import java.util.Date

class SqliteHistoryRepository: BaseHistoryRepository {
    override suspend fun getMealsForDay(date: Date): List<Meal> {
        TODO("Not yet implemented")
    }

    override suspend fun addFoodToMeal(name: String, foodId: FoodComponentId) {
        TODO("Not yet implemented")
    }

    override suspend fun removeFoodFromMeal(name: String, foodId: FoodComponentId) {
        TODO("Not yet implemented")
    }

    override suspend fun observeHistory(): Flow<History> {
        TODO("Not yet implemented")
    }
}