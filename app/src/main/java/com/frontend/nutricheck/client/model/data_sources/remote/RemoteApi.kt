package com.frontend.nutricheck.client.model.data_sources.remote

import com.frontend.nutricheck.client.dto.FoodProductDTO
import com.frontend.nutricheck.client.dto.MealDTO
import com.frontend.nutricheck.client.dto.RecipeDTO
import com.frontend.nutricheck.client.dto.ReportDTO
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Defines the API endpoints for user-related operations to interact with the backend.
 */

interface RemoteApi {

    //User: Recipes
    /**
     * Uploads a personal recipe to the server.
     *
     * @param recipe The recipe to be uploaded.
     * @return A Response containing the uploaded RecipeDTO.
     */
    @POST("/user/recipes")
    suspend fun uploadRecipe(@Body recipe: RecipeDTO): Response<RecipeDTO>

    /**
     * Reports a recipe for inappropriate content.
     *
     * @param recipeReport The report details including description and recipe ID.
     * @return A Response containing the ReportDTO.
     */
    @POST("/user/recipes/report")
    suspend fun reportRecipe(@Body recipeReport: ReportDTO): Response<ReportDTO>

    //User: Search and Meal
    /**
     * Searches for food products by name.
     *
     * @param name The name of the food product to search for.
     * @param language The language in which the results should be returned.
     * @return A Response containing a list of FoodProductDTO matching the search criteria.
     */
    @GET("/user/search/products/{name}")
    suspend fun searchFoodProduct(@Path("name") name: String,
                                  @Query("language") language: String): Response<List<FoodProductDTO>>

    /**
     * Searches for recipes by name.
     *
     * @param name The name of the recipe to search for.
     * @return A Response containing a list of RecipeDTO matching the search criteria.
     */
    @GET("/user/search/recipes/{name}")
    suspend fun searchRecipes(@Path("name") name: String): Response<List<RecipeDTO>>

    /**
     * Estimates a meal based on an uploaded image.
     *
     * @param file The image file to be analyzed for meal estimation.
     * @return A Response containing the estimated MealDTO.
     */
    @Multipart
    @POST("/user/meal")
    suspend fun estimateMeal(@Part file: MultipartBody.Part,
                             @Query("language") language: String): Response<MealDTO>
}