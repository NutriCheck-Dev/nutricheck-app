package com.frontend.nutricheck.client.model.repositories.recipe

import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    suspend fun searchRecipe(recipeName: String): List<Recipe>
    suspend fun saveRecipe(recipe: Recipe)
    suspend fun deleteRecipe(recipeId: String)
    suspend fun getAllRecipes(): Flow<List<Recipe>>
    suspend fun createRecipe(recipe: Recipe)
    suspend fun changeRecipe(recipe: Recipe)
}