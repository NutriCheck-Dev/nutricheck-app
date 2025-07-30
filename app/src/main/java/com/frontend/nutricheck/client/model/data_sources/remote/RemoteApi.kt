package com.frontend.nutricheck.client.model.data_sources.remote

import com.frontend.nutricheck.client.dto.FoodProductDTO
import com.frontend.nutricheck.client.dto.MealDTO
import com.frontend.nutricheck.client.dto.RecipeDTO
import com.frontend.nutricheck.client.dto.ReportDTO
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RemoteApi {

    //User: Recipes
    @POST("/user/recipes")
    suspend fun uploadRecipe(@Body recipe: RecipeDTO): Response<RecipeDTO>

    @GET("/user/recipes/{recipeId}")
    suspend fun downloadRecipe(@Path("recipeId") recipeId: String): Response<RecipeDTO>

    @POST("/user/recipes/report")
    suspend fun reportRecipe(@Body recipeReport: ReportDTO): Response<ReportDTO>

    //User: Search and Meal
    @GET("/user/search/products/{name}")
    suspend fun searchFoodProduct(@Path("name") name: String,
                                  @Query("language") language: String ): Response<List<FoodProductDTO>>

    @GET("/user/search/recipes/{name}")
    suspend fun searchRecipes(@Path("name") name: String): Response<List<RecipeDTO>>

    //TODO: How to pass picture
    @POST("/user/meal")
    suspend fun estimateMeal(@Body file: MultipartBody.Part): Response<MealDTO>
}