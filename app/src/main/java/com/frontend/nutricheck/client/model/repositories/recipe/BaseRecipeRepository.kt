package com.frontend.nutricheck.client.model.repositories.recipe

import com.frontend.nutricheck.client.model.data_layer.Recipe
import com.frontend.nutricheck.client.model.logic.commands.CommandInvoker

interface BaseRecipeRepository {
    val commandInvoker: CommandInvoker
    suspend fun getRecipe(recipeId: String): Recipe?
    suspend fun saveRecipe(recipe: Recipe)
    suspend fun deleteRecipe(recipeId: String)
}