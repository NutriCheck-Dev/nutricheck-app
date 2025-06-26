package com.frontend.nutricheck.client.model.repositories.api

import com.frontend.nutricheck.client.model.data_layer.FoodProduct
import com.frontend.nutricheck.client.model.data_layer.Meal
import com.frontend.nutricheck.client.model.data_layer.Recipe
import com.frontend.nutricheck.client.model.data_layer.RemoteApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiRepositoryImpl : BaseApiRepository {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://deine-api-url.de/") // Basis-URL anpassen
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val api = retrofit.create(RemoteApi::class.java)
    override suspend fun getFoodProductsByQuery(query: String): Result<List<FoodProduct>> {
        TODO("Not yet implemented")
        api.searchFoodProduct(query)
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