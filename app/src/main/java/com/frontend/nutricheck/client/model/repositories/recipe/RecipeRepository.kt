package com.frontend.nutricheck.client.model.repositories.recipe

import com.frontend.nutricheck.client.model.data_layer.Recipe
import com.frontend.nutricheck.client.model.logic.commands.CommandInvoker

interface RecipeRepository {
    val commandInvoker: CommandInvoker
    suspend fun searchRecipe(recipeName: String): List<Recipe>
    suspend fun saveRecipe(recipe: Recipe)
    suspend fun deleteRecipe(recipeId: String)
    suspend fun getAllRecipes(): List<Recipe>
    suspend fun createRecipe(recipe: Recipe)
    suspend fun changeRecipe(recipe: Recipe)
}