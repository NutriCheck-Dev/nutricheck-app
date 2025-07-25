package com.frontend.nutricheck.client.model.data_sources.remote

import com.frontend.nutricheck.client.dto.FoodProductDTO
import com.frontend.nutricheck.client.dto.MealDTO
import com.frontend.nutricheck.client.dto.RecipeDTO
import com.frontend.nutricheck.client.dto.ReportDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RemoteApi {

    // Rezept hochladen (POST)
    @POST("/user/recipes")
    suspend fun uploadRecipe(@Body recipe: RecipeDTO): Response<RecipeDTO>

    // Rezept herunterladen (GET)
    @GET("/user/recipes/{recipeId}")
    suspend fun downloadRecipe(@Path("recipeId") recipeId: String): Response<RecipeDTO>

    // Meldeantrag hochladen (POST)
    @POST("/user/recipes/report")
    suspend fun reportRecipe(@Body recipeReport: ReportDTO): Response<ReportDTO>

    // Lebensmittel suchen, mit language Query-Parameter (default "de")
    @GET("/search/products/{name}")
    suspend fun searchFoodProduct(
        @Path("name") name: String,
        @Query("language") language: String = "de"
    ): Response<List<FoodProductDTO>>

    // Rezepte suchen
    @GET("/search/recipes/{name}")
    suspend fun getRecipes(@Path("name") name: String): Response<List<RecipeDTO>>

    // Mahlzeit schätzen lassen (POST)
    @POST("/meal")
    suspend fun estimateMeal(@Body picture: String): Response<MealDTO>
}