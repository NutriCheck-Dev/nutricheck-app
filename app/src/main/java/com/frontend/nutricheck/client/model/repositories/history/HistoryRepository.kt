package com.frontend.nutricheck.client.model.repositories.history

import com.frontend.nutricheck.client.model.data_layer.HistoryDay
import com.frontend.nutricheck.client.model.data_layer.Meal
import com.frontend.nutricheck.client.model.logic.commands.CommandInvoker
import java.util.Date

interface HistoryRepository {
    val commandInvoker: CommandInvoker
    suspend fun getCalorieHistory(): List<HistoryDay>
    suspend fun getDailyHistory(date: Date): HistoryDay
    suspend fun requestAiMeal()
    suspend fun deleteMeal(meal: Meal)
    suspend fun updateMeal(meal: Meal)
    suspend fun getMealsForDay(date: Date): List<Meal>
    suspend fun addFoodToMeal(name: String, foodId: String = "")
    suspend fun removeFoodFromMeal(name: String, foodId: String = "")
    suspend fun getHistoryById(id: String): HistoryDay?
    suspend fun addMeal(meal: Meal)
    suspend fun saveAsRecipe(meal: Meal, recipeName: String = "", recipeDescription: String = "")
}