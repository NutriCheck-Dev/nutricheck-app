package com.frontend.nutricheck.client.model.repositories.recipe

import com.frontend.nutricheck.client.dto.ReportDTO
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.RecipeReport
import com.frontend.nutricheck.client.model.data_sources.data.Result
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    suspend fun searchRecipes(recipeName: String): Flow<Result<List<Recipe>>>
    suspend fun insertRecipe(recipe: Recipe)
    suspend fun deleteRecipe(recipe: Recipe)
    suspend fun getMyRecipes(): List<Recipe>
    suspend fun getRecipeById(recipeId: String): Recipe
    suspend fun updateRecipe(recipe: Recipe)
    suspend fun uploadRecipe(recipe: Recipe): Result<Recipe>
    suspend fun reportRecipe(recipeReport: RecipeReport): Result<ReportDTO>
    suspend fun getIngredientById(recipeId: String, foodProductId: String): Ingredient
    suspend fun updateIngredient(ingredient: Ingredient)
}