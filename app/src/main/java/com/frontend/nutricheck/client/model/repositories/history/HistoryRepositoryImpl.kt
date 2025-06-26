package com.frontend.nutricheck.client.model.repositories.history

import android.content.Context
import com.frontend.nutricheck.client.model.data_layer.HistoryDay
import com.frontend.nutricheck.client.model.data_layer.Meal
import com.frontend.nutricheck.client.model.logic.commands.CommandInvoker
import com.frontend.nutricheck.client.model.persistence.DatabaseProvider
import java.util.Date

class HistoryRepositoryImpl(context: Context) : HistoryRepository {
    private val invoker = CommandInvoker()
    override val commandInvoker: CommandInvoker
        get() = invoker

    private val historyDao = DatabaseProvider.getDatabase(context).historyDao()
    override suspend fun getCalorieHistory(): List<HistoryDay> {
        TODO("Not yet implemented")
    }

    override suspend fun getDailyHistory(date: Date): HistoryDay {
        TODO("Not yet implemented")
    }

    override suspend fun requestAiMeal() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMeal(meal: Meal) {
        TODO("Not yet implemented")
    }

    override suspend fun updateMeal(meal: Meal) {
        TODO("Not yet implemented")
    }

    override suspend fun getMealsForDay(date: Date): List<Meal> {
        TODO("Not yet implemented")
    }

    override suspend fun addFoodToMeal(name: String, foodId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun removeFoodFromMeal(name: String, foodId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getHistoryById(id: String): HistoryDay? = historyDao.getById(id)
    override suspend fun addMeal(meal: Meal) {
        TODO("Not yet implemented")
    }

    override suspend fun saveAsRecipe(
        meal: Meal,
        recipeName: String,
        recipeDescription: String
    ) {
        TODO("Not yet implemented")
    }
}