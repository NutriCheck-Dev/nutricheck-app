package com.frontend.nutricheck.client.model.repositories.recipe

import android.content.Context
import com.frontend.nutricheck.client.model.data_layer.Recipe
import com.frontend.nutricheck.client.model.logic.commands.CommandInvoker

class ApiRecipeRepository(private val context: Context) : BaseRecipeRepository {
    private val invoker = CommandInvoker()
    override val commandInvoker: CommandInvoker
        get() = invoker

    override suspend fun getRecipe(recipeId: String): Recipe? {
        TODO("Not yet implemented")
    }

    override suspend fun saveRecipe(recipe: Recipe) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRecipe(recipeId: String) {
        TODO("Not yet implemented")
    }

}