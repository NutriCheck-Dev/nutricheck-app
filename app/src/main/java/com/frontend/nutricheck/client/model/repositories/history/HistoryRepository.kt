package com.frontend.nutricheck.client.model.repositories.history

import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.MealItem
import com.frontend.nutricheck.client.model.data_sources.data.MealRecipeItem
import com.frontend.nutricheck.client.model.data_sources.data.Result
import okhttp3.MultipartBody
import java.util.Date

interface HistoryRepository {
    /**
     * Retrieves the sum of all calories from all the meals for a specific day.
     */
    suspend fun getCaloriesOfDay(date: Date): Int
    /**
     * Requests the AI to estimate the nutriments of a meal based on a photo.
     */
    suspend fun requestAiMeal(file: MultipartBody.Part): Result<Meal>
    /**
     * Deletes a meal from the database.
     */
    suspend fun deleteMeal(meal: Meal)
    /**
     * Updates an existing meal in the database.
     */
    suspend fun updateMeal(meal: Meal)
    /**
     * Retrieves all the meals of a specified date.
     */
    suspend fun getMealsForDay(date: Date): List<Meal>
    suspend fun removeMealItem(mealItem: MealItem)
    suspend fun getMealById(mealId: String): Meal

    /**
     * Adds a new meal to the database.
     */
    suspend fun addMeal(meal: Meal)

    /**
     * Gets all the macros for the day, including calories, proteins, fats, and carbohydrates.
     */
    suspend fun getDailyMacros() : List<Int>
    suspend fun getMealRecipeItemById(mealId: String, recipeId: String): MealRecipeItem
    suspend fun updateMealFoodItem(mealFoodItem: MealFoodItem)
    suspend fun getMealFoodItemById(mealId: String, foodProductId: String): MealFoodItem
    suspend fun updateMealRecipeItem(mealRecipeItem: MealRecipeItem)
}