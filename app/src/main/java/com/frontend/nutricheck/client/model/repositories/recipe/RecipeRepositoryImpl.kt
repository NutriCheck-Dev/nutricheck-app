package com.frontend.nutricheck.client.model.repositories.recipe

import android.content.Context
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.persistence.DatabaseProvider
import kotlinx.coroutines.flow.Flow

class RecipeRepositoryImpl(context: Context) : RecipeRepository {
    private val recipeDao = DatabaseProvider.getDatabase(context).recipeDao()

    override suspend fun searchRecipe(recipeName: String): List<Recipe> {
        TODO("Not yet implemented")
    }

    override suspend fun saveRecipe(recipe: Recipe) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRecipe(recipeId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllRecipes(): Flow<List<Recipe>> = recipeDao.getAll()

    override suspend fun createRecipe(recipe: Recipe) {
        TODO("Not yet implemented")
    }

    override suspend fun changeRecipe(recipe: Recipe) {
        TODO("Not yet implemented")
    }
}