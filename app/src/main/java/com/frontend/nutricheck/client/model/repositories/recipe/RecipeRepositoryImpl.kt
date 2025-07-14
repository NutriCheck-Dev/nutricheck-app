package com.frontend.nutricheck.client.model.repositories.recipe

import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.RecipeDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val recipeDao: RecipeDao
) : RecipeRepository {

    override suspend fun insertRecipe(recipe: Recipe) = recipeDao.insert(recipe)

    override suspend fun deleteRecipe(recipe: Recipe) = recipeDao.delete(recipe)
    override suspend fun searchRecipe(recipeName: String): List<Recipe> {
        TODO("Not yet implemented")
    }

    override fun getMyRecipes(): Flow<List<Recipe>> = recipeDao.getAll()
    override fun getOnlineRecipes(): Flow<List<Recipe>> {
        TODO("Not yet implemented")
    }

    override fun getRecipeById(recipeId: String) = recipeDao.getById(recipeId)

    override suspend fun updateRecipe(recipe: Recipe) = recipeDao.update(recipe)
}