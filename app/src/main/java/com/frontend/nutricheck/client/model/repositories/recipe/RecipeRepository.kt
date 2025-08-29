package com.frontend.nutricheck.client.model.repositories.recipe

import com.frontend.nutricheck.client.dto.ReportDTO
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.RecipeReport
import com.frontend.nutricheck.client.model.data_sources.data.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing recipes.
 */
interface RecipeRepository {
    /**
     * Searches for recipes by name using the API.
     */
    fun searchRecipes(recipeName: String): Flow<Result<List<Recipe>>>
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

    /**
     * Retrieves an ingredient by recipeId and foodProductId.
     */
    suspend fun getIngredientById(recipeId: String, foodProductId: String): Ingredient
    /**
     * Updates an existing ingredient in the database.
     */
    suspend fun updateIngredient(ingredient: Ingredient)
    /**
     * Returns a flow that emits a recipe by id whenever it changes in the data.
     */
    fun observeRecipeById(recipeId: String): Flow<Recipe>
    /**
     * Returns a flow that emits the list of all local saved recipes whenever it changes in the data.
     */

    fun observeMyRecipes(): Flow<List<Recipe>>
    /**
     * Downloads a recipe from the server and saves it to the local database.
     */
    suspend fun downloadRecipe(recipe: Recipe)
}