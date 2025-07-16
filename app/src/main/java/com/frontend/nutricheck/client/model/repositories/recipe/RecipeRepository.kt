package com.frontend.nutricheck.client.model.repositories.recipe

import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    suspend fun searchRecipe(recipeName: String): List<Recipe>
    suspend fun insertRecipe(recipe: Recipe)
    suspend fun deleteRecipe(recipe: Recipe)
    fun getMyRecipes(): Flow<List<Recipe>>
    fun getOnlineRecipes(): Flow<List<Recipe>>
    fun getRecipeById(recipeId: String): Flow<Recipe>
    suspend fun updateRecipe(recipe: Recipe)
}