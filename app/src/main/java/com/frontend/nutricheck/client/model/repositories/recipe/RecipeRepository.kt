package com.frontend.nutricheck.client.model.repositories.recipe

import com.frontend.nutricheck.client.dto.ReportDTO
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.RecipeReport
import com.frontend.nutricheck.client.model.data_sources.data.Result
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    /**
     * Searches for recipes by name using the API.
     */
    suspend fun searchRecipes(recipeName: String): Flow<Result<List<Recipe>>>
    /**
     * Inserts a new recipe into the database.
     */
    suspend fun insertRecipe(recipe: Recipe)
    /**
     * Deletes a recipe from the database.
     */
    suspend fun deleteRecipe(recipe: Recipe)
    /**
     * Retrieves all local saved recipes from the database.
     */
    suspend fun getMyRecipes(): List<Recipe>

    /**
     * Retrieves a recipe by its ID.
     */
    suspend fun getRecipeById(recipeId: String): Recipe

    /**
     * Updates an existing recipe in the database.
     */
    suspend fun updateRecipe(recipe: Recipe)
    /**
     * Uploads a recipe to the server.
     */
    suspend fun uploadRecipe(recipe: Recipe): Result<Recipe>
    /**
     * Reports a recipe to the server.
     */
    suspend fun reportRecipe(recipeReport: RecipeReport): Result<ReportDTO>
    suspend fun getIngredientById(recipeId: String, foodProductId: String): Ingredient
    suspend fun updateIngredient(ingredient: Ingredient)
    suspend fun getRecipesByName(recipeName: String): List<Recipe>
    fun observeRecipeById(recipeId: String): Flow<Recipe>
    suspend fun observeMyRecipes(): Flow<List<Recipe>>
    suspend fun downloadRecipe(recipe: Recipe)
}