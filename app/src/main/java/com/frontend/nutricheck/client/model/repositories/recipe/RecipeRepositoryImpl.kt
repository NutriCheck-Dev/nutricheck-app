package com.frontend.nutricheck.client.model.repositories.recipe

import com.frontend.nutricheck.client.dto.ErrorResponseDTO
import com.frontend.nutricheck.client.dto.ReportDTO
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.RecipeReport
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.IngredientDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.RecipeDao
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbIngredientMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbRecipeMapper
import com.frontend.nutricheck.client.model.data_sources.remote.RemoteApi
import com.frontend.nutricheck.client.model.data_sources.remote.RetrofitInstance
import com.frontend.nutricheck.client.model.repositories.mapper.RecipeMapper
import com.frontend.nutricheck.client.model.repositories.mapper.ReportMapper
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import java.io.IOException
import javax.inject.Inject
import kotlin.jvm.java

class RecipeRepositoryImpl @Inject constructor(
    private val recipeDao: RecipeDao,
    private val ingredientDao: IngredientDao,
) : RecipeRepository {
    private val api = RetrofitInstance.getInstance().create(RemoteApi::class.java)
    override suspend fun searchRecipe(recipeName: String): Result<List<Recipe>> {
        return try {
            val response = api.searchRecipes(recipeName)
            val body = response.body()
            val errorBody = response.errorBody()

            if (response.isSuccessful && body != null) {
                val recipes: List<Recipe> = body.map { RecipeMapper.toEntity(it) }
                Result.Success(recipes)
            } else if (errorBody != null) {
                val gson = Gson()
                val errorResponse = gson.fromJson(
                    String(errorBody.bytes()),
                    ErrorResponseDTO::class.java
                )
                val message = errorResponse.title + errorResponse.detail
                Result.Error(errorResponse.status, message)
            } else {
                Result.Error(message = "Unknown error")
            }
        } catch (e: IOException) {
            Result.Error(message = "Connection issue>")
        }
    }

    override suspend fun insertRecipe(recipe: Recipe) {
        val recipeEntity = DbRecipeMapper.toRecipeEntity(recipe)
        recipeDao.insert(recipeEntity)
        recipe.ingredients.forEach { ingredient ->
            val ingredientEntity = DbIngredientMapper.toIngredientEntity(ingredient)
            ingredientDao.insert(ingredientEntity)
        }
    }

    override suspend fun getMyRecipes(): List<Recipe> {
        val recipesWithIngredients = recipeDao.getAllRecipesWithIngredients()
        val list = recipesWithIngredients.first()
        return list.map { recipeWithIngredients ->
            DbRecipeMapper.toRecipe(recipeWithIngredients)
        }
    }

    override suspend fun getRecipeById(recipeId: String): Recipe {
        val recipeWithIngredients = recipeDao.getRecipeWithIngredientsById(recipeId).first()
        return DbRecipeMapper.toRecipe(recipeWithIngredients)
    }

    override suspend fun deleteRecipe(recipe: Recipe) {
        val recipeEntity = DbRecipeMapper.toRecipeEntity(recipe)
        recipeDao.delete(recipeEntity)
        //ingredientDao.deleteIngredientsOfRecipe(recipe.id) unnecessary because of cascade?
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        val recipeEntity = DbRecipeMapper.toRecipeEntity(recipe)
        val ingredientsEntity = recipe.ingredients.map { DbIngredientMapper.toIngredientEntity(it) }
        recipeDao.update(recipeEntity)
        ingredientDao.deleteIngredientsOfRecipe(recipeEntity.id)
        ingredientsEntity.forEach { ingredientDao.insert(it) }
    }

    override suspend fun uploadRecipe(recipe: Recipe): Result<Recipe> {
        return try {
            val response = api.uploadRecipe(RecipeMapper.toDto(recipe))
            val body = response.body()
            val errorBody = response.errorBody()

            if (response.isSuccessful && body != null) {
                Result.Success(RecipeMapper.toEntity(body))
            } else if (errorBody != null) {
                val gson = Gson()
                val errorResponse = gson.fromJson(
                    String(errorBody.bytes()),
                    ErrorResponseDTO::class.java
                )
                val message = errorResponse.title + errorResponse.detail
                Result.Error(errorResponse.status, message)
            } else {
                Result.Error(message = "Unknown error")
            }
        } catch (io: IOException) {
            Result.Error(message = "Connection issue")
        }
    }

    override suspend fun reportRecipe(recipeReport: RecipeReport): Result<ReportDTO> {
        return try {
            val response = api.reportRecipe(ReportMapper.toDto(recipeReport))
            val body = response.body()
            val errorBody = response.errorBody()

            if (response.isSuccessful && body != null) {
                Result.Success(data = TODO()) //toEntity method
            } else if (errorBody != null) {
                val gson = Gson()
                val errorResponse = gson.fromJson(
                    String(errorBody.bytes()),
                    ErrorResponseDTO::class.java
                )
                val message = errorResponse.title + errorResponse.detail
                Result.Error(errorResponse.status, message)
            } else {
                Result.Error(message = "Unknown error")
            }
        } catch (io: IOException) {
            Result.Error(message = "Connection issue")
        }
    }

    override suspend fun getIngredientById(
        recipeId: String,
        foodProductId: String
    ): Ingredient {
        val ingredientWithFoodProduct = ingredientDao.getIngredientById(recipeId, foodProductId).first()
        return DbIngredientMapper.toIngredient(ingredientWithFoodProduct!!)
    }

    //will maybe be removed
    override suspend fun downloadRecipe(recipeId: String): Result<Recipe> {
        val response = api.downloadRecipe(recipeId)
        return try {
            if (response.isSuccessful) {
                val recipeDto = response.body()
                if (recipeDto != null) {
                    Result.Success(RecipeMapper.toEntity(recipeDto))
                } else {
                    Result.Error(message = "Leeres Rezept erhalten.")
                }
            } else {
                Result.Error(code = response.code(), message = response.errorBody()?.string())
            }
        } catch (io: IOException) {
            Result.Error(message = "Bitte überprüfen Sie Ihre Internetverbindung.")
        }
    }

    //necessary? because do we know if one specific ingredient is updated or do we update after whole recipe is updated??
    override suspend fun addIngredient(ingredient: Ingredient) {
        val ingredientEntity = DbIngredientMapper.toIngredientEntity(ingredient)
        ingredientDao.insert(ingredientEntity)
    }

    override suspend fun updateIngredient(ingredient: Ingredient) {
        val ingredientEntity = DbIngredientMapper.toIngredientEntity(ingredient)
        ingredientDao.update(ingredientEntity)
    }

    override suspend fun removeIngredient(ingredient: Ingredient) {
        val ingredientEntity = DbIngredientMapper.toIngredientEntity(ingredient)
        ingredientDao.delete(ingredientEntity)
    }
}