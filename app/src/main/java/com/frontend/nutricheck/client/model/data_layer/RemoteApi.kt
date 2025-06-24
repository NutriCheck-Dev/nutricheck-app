package com.frontend.nutricheck.client.model.data_layer

import com.frontend.nutricheck.client.dto.FoodProductDTO
import com.frontend.nutricheck.client.dto.MealDTO
import com.frontend.nutricheck.client.dto.RecipeDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RemoteApi {
    @GET("/search/product/{name}")
    suspend fun searchFoodProduct(@Path("name") name: String): Response<List<FoodProductDTO>>

    @GET("/search/recipe/{name}")
    suspend fun getRecipes(@Path("name") name: String): Response<List<RecipeDTO>>

    @POST("/recipe/upload")
    suspend fun uploadRecipe(@Body recipe: RecipeDTO): Response<Void>

    @POST("/recipe/report")
    suspend fun reportRecipe(@Body recipeReport: RecipeReport): Response<Void>

    @GET("/recipe/download/{recipeId}")
    suspend fun downloadRecipe(@Path("recipeID") recipeID: String): Response<RecipeDTO>

    @GET("/ai/estimate")
    suspend fun estimateRecipe(): Response<MealDTO>
}