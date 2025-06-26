package com.frontend.nutricheck.client.model.repositories.api

import com.frontend.nutricheck.client.model.data_layer.FoodProduct
import com.frontend.nutricheck.client.model.data_layer.Meal
import com.frontend.nutricheck.client.model.data_layer.Recipe

class ApiRepositoryImpl : BaseApiRepository {
    override suspend fun getFoodProductsByQuery(query: String): Result<List<FoodProduct>> {
        TODO("Not yet implemented")
    }
    override suspend fun uploadRecipe(recipe: Recipe): Result<String> {
        TODO("Not yet implemented")
    }
    override suspend fun getAiMeal(): Result<Meal> {
        TODO("Not yet implemented")
    }

    override suspend fun getRecipesByQuery(query: String): Result<List<Recipe>> {
        TODO("Not yet implemented")
    }

    override suspend fun reportRecipe(recipeId: String, description: String): Result<String> {
        TODO("Not yet implemented")
    }
}