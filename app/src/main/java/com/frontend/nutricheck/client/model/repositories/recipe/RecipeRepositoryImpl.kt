package com.frontend.nutricheck.client.model.repositories.recipe

import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.RecipeDao
import kotlinx.coroutines.flow.Flow

class RecipeRepositoryImpl(private val recipeDao: RecipeDao) : RecipeRepository {

    override suspend fun searchRecipe(recipeName: String): List<Recipe> {
        TODO("Not yet implemented")
    }

    override suspend fun saveRecipe(recipe: Recipe) = recipeDao.insert(recipe)

    override suspend fun deleteRecipe(recipe: Recipe) = recipeDao.delete(recipe)

    override suspend fun getAllRecipes(): Flow<List<Recipe>> = recipeDao.getAll()

    override suspend fun getRecipeById(recipeId: String) = recipeDao.getById(recipeId)

    override suspend fun updateRecipe(recipe: Recipe) = recipeDao.update(recipe)
}