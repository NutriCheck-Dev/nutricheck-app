package com.frontend.nutricheck.client.model.repositories.recipe

import android.content.Context
import com.frontend.nutricheck.client.model.data_layer.Recipe
import com.frontend.nutricheck.client.model.persistence.DatabaseProvider

class RecipeRepositoryImpl(private val context: Context) : RecipeRepository {
    private val recipeDao = DatabaseProvider.getDatabase(context).recipeDao()

    override suspend fun createRecipe(recipe: Recipe) {
        TODO("Not yet implemented")
    }

    override suspend fun changeRecipe(recipe: Recipe) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRecipe(recipeId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun uploadRecipe(recipe: Recipe): Result<String> {
        TODO("Not yet implemented")
    }


    override suspend fun searchRecipeLocal(recipeName: String): List<Recipe> {
        TODO("Not yet implemented")
    }

    override suspend fun searchRecipeRemote(recipeName: String): List<Recipe> {
        TODO("Not yet implemented")
    }

    override suspend fun saveRecipe(recipe: Recipe) {
        TODO("Not yet implemented")
    }

    override suspend fun reportRecipe(recipeId: String, description: String): Result<String> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllRecipes(): List<Recipe> = recipeDao.getAll()
}