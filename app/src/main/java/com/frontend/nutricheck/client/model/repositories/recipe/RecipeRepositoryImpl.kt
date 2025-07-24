package com.frontend.nutricheck.client.model.repositories.recipe

import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.IngredientDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.RecipeDao
import com.frontend.nutricheck.client.model.data_sources.remote.RemoteApi
import com.frontend.nutricheck.client.model.data_sources.remote.RetrofitInstance
import com.frontend.nutricheck.client.model.repositories.mapper.RecipeMapper
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val recipeDao: RecipeDao,
    private val ingredientDao: IngredientDao,
) : RecipeRepository {
    private val api = RetrofitInstance.getInstance().create(RemoteApi::class.java)

    override suspend fun insertRecipe(recipe: Recipe) =
        recipeDao.insert(recipe)

    override suspend fun deleteRecipe(recipe: Recipe) =
        recipeDao.delete(recipe)
    override suspend fun searchRecipe(recipeName: String): Result<List<Recipe>> {
        return try {
            val response = api.getRecipes(recipeName)
            if (response.isSuccessful) {
                val dtos = response.body().orEmpty()
                val recipes: List<Recipe> = dtos.map { RecipeMapper.toEntity(it) }
                Result.Success(recipes)
            } else {
                Result.Error(code = response.code(), message = response.errorBody()?.string())
            }
        } catch (io: IOException) {
            Result.Error(message = "Bitte überprüfen Sie Ihre Internetverbindung.")
        }
    }

    override fun getMyRecipes(): Flow<List<Recipe>> =
        recipeDao.getAll()

    override fun getOnlineRecipes(): Flow<List<Recipe>> {
        TODO("Not yet implemented")
    }

    override fun getRecipeById(recipeId: String) =
        recipeDao.getById(recipeId)

    override suspend fun updateRecipe(recipe: Recipe) =
        recipeDao.update(recipe)

    override fun getIngredientsForRecipe(recipeId: String): Flow<List<Ingredient>> =
        ingredientDao.getForRecipe(recipeId)

    override suspend fun addIngredient(ingredient: Ingredient) =
        ingredientDao.insert(ingredient)

    override suspend fun updateIngredient(ingredient: Ingredient) =
        ingredientDao.update(ingredient)

    override suspend fun removeIngredient(ingredient: Ingredient) =
        ingredientDao.delete(ingredient)
}