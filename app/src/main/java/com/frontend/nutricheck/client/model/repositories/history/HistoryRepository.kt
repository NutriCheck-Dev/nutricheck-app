package com.frontend.nutricheck.client.model.repositories.history

import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.RecipeEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealWithAll
import java.util.Date

interface HistoryRepository {
    suspend fun getCaloriesOfDay(date: Date): Int
    suspend fun requestAiMeal(): MealEntity
    suspend fun deleteMeal(meal: MealEntity)
    suspend fun updateMeal(meal: MealEntity)
    suspend fun getMealsForDay(date: Date): List<MealWithAll>
    suspend fun addFoodToMeal(name: String, foodId: String = "")
    suspend fun removeFoodFromMeal(name: String, foodId: String = "")
    suspend fun addMeal(meal: MealEntity, mealFoodItemsWithProduct: List<Pair<Double, FoodProductEntity>>?, mealRecipeItemsWithRecipeEntity: List<Pair<Double, RecipeEntity>>?)
    suspend fun saveAsRecipe(meal: MealEntity, recipeName: String = "", recipeDescription: String = "")
}