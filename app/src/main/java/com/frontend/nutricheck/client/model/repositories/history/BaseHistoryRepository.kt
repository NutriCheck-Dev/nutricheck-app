package com.frontend.nutricheck.client.model.repositories.history

import com.frontend.nutricheck.client.model.data_layer.HistoryDay
import com.frontend.nutricheck.client.model.data_layer.Meal
import com.frontend.nutricheck.client.model.logic.commands.CommandInvoker
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface BaseHistoryRepository {
    val commandInvoker: CommandInvoker
    suspend fun getCalorieHistory(): List<HistoryDay>
    suspend fun getMealsForDay(date: Date): List<Meal>
    suspend fun addFoodToMeal(name: String, foodId: String = "")
    suspend fun removeFoodFromMeal(name: String, foodId: String = "")
    suspend fun observeHistory(): Flow<HistoryDay>
}