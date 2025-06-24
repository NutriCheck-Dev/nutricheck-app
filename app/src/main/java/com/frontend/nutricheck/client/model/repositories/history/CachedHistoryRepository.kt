package com.frontend.nutricheck.client.model.repositories.history

import com.frontend.nutricheck.client.model.data_layer.History
import com.frontend.nutricheck.client.model.data_layer.Meal
import com.frontend.nutricheck.client.model.logic.commands.CommandInvoker
import kotlinx.coroutines.flow.Flow
import java.util.Date

class CachedHistoryRepository: BaseHistoryRepository {
    private val invoker = CommandInvoker()
    override val commandInvoker: CommandInvoker
        get() = invoker

    override suspend fun getMealsForDay(date: Date): List<Meal> {
        TODO("Not yet implemented")
    }

    override suspend fun addFoodToMeal(name: String, foodId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun removeFoodFromMeal(name: String, foodId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun observeHistory(): Flow<History> {
        TODO("Not yet implemented")
    }
}