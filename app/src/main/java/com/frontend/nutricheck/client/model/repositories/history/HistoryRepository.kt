package com.frontend.nutricheck.client.model.repositories.history

import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.MealItem
import com.frontend.nutricheck.client.model.data_sources.data.MealRecipeItem
import com.frontend.nutricheck.client.model.data_sources.data.Result
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import java.util.Date

/**
 * Repository interface for managing the history of meals and their related data.
 */
interface HistoryRepository {
    /**
     * Retrieves the sum of all calories from all the meals for a specific day.
     */
    suspend fun getCaloriesOfDay(date: Date): Int
    /**
     * Requests the AI to estimate the nutriments of a meal based on a photo.
     */
    suspend fun requestAiMeal(file: MultipartBody.Part, language: String): Result<Meal>
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

    /**
     * Removes a meal item (either food or recipe) from a meal.
     */
    suspend fun removeMealItem(mealItem: MealItem)
    /**
     * Retrieves a meal by its ID.
     */
    suspend fun getMealById(mealId: String): Meal
    /**
     * Adds a new meal to the database.
     */
    suspend fun addMeal(meal: Meal)

    /**
     * Gets all the macros for the day, including calories, proteins, fats, and carbohydrates.
     */
    suspend fun getDailyMacros() : List<Int>

    /**
     * Returns the mealRecipeItem by mealId and recipeId.
     */
    suspend fun getMealRecipeItemById(mealId: String, recipeId: String): MealRecipeItem

    /**
     * Updates a given mealFoodItem in the database.
     */
    suspend fun updateMealFoodItem(mealFoodItem: MealFoodItem)
    /**
     * Returns the mealFoodItem by mealId and foodProductId.
     */
    suspend fun getMealFoodItemById(mealId: String, foodProductId: String): MealFoodItem
    /**
     * Updates a given mealRecipeItem in the database.
     */
    suspend fun updateMealRecipeItem(mealRecipeItem: MealRecipeItem)

    /**
     * Returns a flow that emits the list of meals for a specific day whenever there is a change in the data.
     */
    suspend fun observeMealsForDay(date: Date): Flow<List<Meal>>
    /**
     * Returns a flow that emits the total calories for a specific day whenever there is a change in the data.
     */
    suspend fun observeCaloriesOfDay(date: Date): Flow<Int>
}