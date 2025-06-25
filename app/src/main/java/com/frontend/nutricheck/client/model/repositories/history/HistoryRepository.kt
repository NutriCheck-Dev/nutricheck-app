package com.frontend.nutricheck.client.model.repositories.history

import android.content.Context
import com.frontend.nutricheck.client.model.data_layer.History
import com.frontend.nutricheck.client.model.data_layer.Meal
import com.frontend.nutricheck.client.model.logic.commands.CommandInvoker
import com.frontend.nutricheck.client.model.persistence.DatabaseProvider
import kotlinx.coroutines.flow.Flow
import java.util.Date

class HistoryRepository(context: Context) : BaseHistoryRepository {
    private val invoker = CommandInvoker()
    override val commandInvoker: CommandInvoker
        get() = invoker

    private val historyDao = DatabaseProvider.getDatabase(context).historyDao()

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

    suspend fun getHistoryById(id: String): History? = historyDao.getById(id)
}