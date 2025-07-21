package com.frontend.nutricheck.client.model.repositories.history

import com.frontend.nutricheck.client.model.data_sources.data.HistoryDay
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.HistoryDao
import com.frontend.nutricheck.client.model.data_sources.remote.RemoteApi
import com.frontend.nutricheck.client.model.data_sources.remote.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao
) : HistoryRepository {
    private val api = RetrofitInstance.getInstance().create(RemoteApi::class.java)

    override suspend fun getCalorieHistory(): List<HistoryDay> {
        TODO("Not yet implemented")
    }

    override suspend fun getDailyHistory(date: Date): HistoryDay {
        TODO("Not yet implemented")
    }

    override suspend fun requestAiMeal(): Meal {
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
    override suspend fun getHistoryByDate(date: Date): Flow<HistoryDay> = historyDao.getByDate(date)

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