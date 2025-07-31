package com.frontend.nutricheck.client.model.repositories.history

import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.RecipeEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealWithAll
import okhttp3.MultipartBody
import java.util.Date

interface HistoryRepository {
    suspend fun getCaloriesOfDay(date: Date): Int
    suspend fun requestAiMeal(file: MultipartBody.Part): Result<Meal>
    suspend fun deleteMeal(meal: Meal)
    suspend fun updateMeal(meal: Meal)
    suspend fun getMealsForDay(date: Date): List<Meal>
    suspend fun addMeal(meal: Meal)
    suspend fun getDailyMacros()
}