package com.frontend.nutricheck.client.model.repositories.history

import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.MealRecipeItem
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.RecipeEntity
import com.forntend.nutrichek.client.model.dat_sources.persistenc.entity.MealEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealWithAll
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import java.util.Date

interface HistoryRepository {
    suspend fun getCaloriesOfDay(date: Date): Int
    suspend fun requestAiMeal(file: MultipartBody.Part): Result<Meal>
    suspend fun deleteMeal(meal: MealEntity)
    suspend fun updateMeal(meal: MealEntity)
    suspend fun getMealsForDay(date: Date): List<MealWithAll>
    suspend fun addFoodToMeal(name: String, foodId: String = "")
    suspend fun removeFoodFromMeal(name: String, foodId: String = "")
    suspend fun getHistoryByDate(date: Date): Flow<HistoryDay>
    suspend fun addMeal(
        meal: MealEntity,
        mealFoodItemsWithProduct: List<Pair<Double, FoodProductEntity>>?,
        mealRecipeItemsWithRecipeEntity: List<Pair<Double, RecipeEntity>>?
    )

    suspend fun saveAsRecipe(
        meal: MealEntity,
        recipeName: String = "",
        recipeDescription: String = ""
    )

    suspend fun getMealFoodItemById(
        mealId: String,
        foodProductId: String
    ): MealFoodItem

    suspend fun updateMealFoodItem(mealFoodItem: MealFoodItem)

    suspend fun getMealRecipeItemById(
        mealId: String,
        recipeId: String
    ): MealRecipeItem

    suspend fun updateMealRecipeItem(mealRecipeItem: MealRecipeItem)
}