package com.frontend.nutricheck.client.model.repositories.recipe

import com.frontend.nutricheck.client.model.data_layer.Recipe

interface RecipeRepository {
    suspend fun createRecipe(recipe: Recipe)
    suspend fun changeRecipe(recipe: Recipe)
    suspend fun deleteRecipe(recipeId: String)
    suspend fun uploadRecipe(recipe: Recipe) : Result<String>
    suspend fun searchRecipeLocal(recipeName: String): List<Recipe>
    suspend fun searchRecipeRemote(recipeName: String): List<Recipe>
    suspend fun saveRecipe(recipe: Recipe)
    suspend fun reportRecipe(recipeId: String, description: String): Result<String>
    suspend fun getAllRecipes(): List<Recipe>
}