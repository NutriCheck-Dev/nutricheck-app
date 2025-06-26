package com.frontend.nutricheck.client.model.repositories.recipe

import com.frontend.nutricheck.client.model.data_layer.Recipe
import com.frontend.nutricheck.client.model.logic.commands.CommandInvoker

interface RecipeRepository {
    val commandInvoker: CommandInvoker
    suspend fun getRecipe(recipeId: String): Recipe?
    suspend fun saveRecipe(recipe: Recipe)
    suspend fun deleteRecipe(recipeId: String)
    suspend fun uploadRecipe(recipe: Recipe): String
    suspend fun getAllRecipes(): List<Recipe>
    suspend fun createRecipe(recipe: Recipe)
    suspend fun reportRecipe(recipeId: String, description: String): Boolean
    suspend fun changeRecipe(recipe: Recipe)
}