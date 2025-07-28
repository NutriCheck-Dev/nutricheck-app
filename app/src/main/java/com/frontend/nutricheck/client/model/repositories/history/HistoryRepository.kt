package com.frontend.nutricheck.client.model.repositories.history

import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.HistoryDay
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Meal
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.RecipeEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealWithAll
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface HistoryRepository {
    suspend fun getCalorieHistory(): List<HistoryDay>
    suspend fun getCaloriesOfDay(date: Date): Int
    suspend fun getDailyHistory(date: Date): HistoryDay
    suspend fun requestAiMeal(): Meal
    suspend fun deleteMeal(meal: Meal)
    suspend fun updateMeal(meal: Meal)
    suspend fun getMealsForDay(date: Date): List<MealWithAll>
    suspend fun addFoodToMeal(name: String, foodId: String = "")
    suspend fun removeFoodFromMeal(name: String, foodId: String = "")
    suspend fun getHistoryByDate(date: Date): Flow<HistoryDay>
    suspend fun addMeal(meal: Meal, mealFoodItemsWithProduct: List<Pair<Double, FoodProductEntity>>?, mealRecipeItemsWithRecipeEntity: List<Pair<Double, RecipeEntity>>?)
    suspend fun saveAsRecipe(meal: Meal, recipeName: String = "", recipeDescription: String = "")
}