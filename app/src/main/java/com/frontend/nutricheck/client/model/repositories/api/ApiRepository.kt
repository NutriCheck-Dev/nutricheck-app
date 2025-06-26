package com.frontend.nutricheck.client.model.repositories.api

import com.frontend.nutricheck.client.model.data_layer.FoodProduct
import com.frontend.nutricheck.client.model.data_layer.Meal
import com.frontend.nutricheck.client.model.data_layer.Recipe

interface BaseApiRepository {
    suspend fun getFoodProductsByQuery(query: String): Result<List<FoodProduct>>
    suspend fun uploadRecipe(recipe: Recipe): Result<String>
    suspend fun getAiMeal(): Result<Meal>
    suspend fun getRecipesByQuery(query: String): Result<List<Recipe>>
    suspend fun reportRecipe(recipeId: String, description: String): Result<String>
}