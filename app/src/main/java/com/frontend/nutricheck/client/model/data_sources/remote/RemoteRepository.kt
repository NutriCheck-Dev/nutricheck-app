package com.frontend.nutricheck.client.model.data_sources.remote

import com.frontend.nutricheck.client.dto.FoodProductDTO
import com.frontend.nutricheck.client.dto.MealDTO
import com.frontend.nutricheck.client.dto.RecipeDTO
import com.frontend.nutricheck.client.dto.ReportDTO
import retrofit2.Response
import javax.inject.Inject

class RemoteRepository @Inject constructor(
    private val remoteApi: RemoteApi
) {
    suspend fun uploadRecipe(recipe: RecipeDTO): Response<RecipeDTO> =
        remoteApi.uploadRecipe(recipe)

    suspend fun downloadRecipe(recipeId: String): Response<RecipeDTO> =
        remoteApi.downloadRecipe(recipeId)

    suspend fun reportRecipe(recipeReport: ReportDTO): Response<ReportDTO> =
        remoteApi.reportRecipe(recipeReport)

    suspend fun searchFoodProduct(name: String, language: String): Response<List<FoodProductDTO>> =
        remoteApi.searchFoodProduct(name, language)

    suspend fun getRecipes(name: String): Response<List<RecipeDTO>> =
        remoteApi.getRecipes(name)

    suspend fun estimateMeal(picture: String): Response<MealDTO> =
        remoteApi.estimateMeal(picture)
}