package com.frontend.nutricheck.client.model.repositories.recipe

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
import kotlinx.coroutines.flow.first
import java.io.IOException
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val recipeDao: RecipeDao,
    private val ingredientDao: IngredientDao,
) : RecipeRepository {
    private val api = RetrofitInstance.getInstance().create(RemoteApi::class.java)
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

    override suspend fun insertRecipe(recipe: Recipe) {
        val recipeEntity = DbRecipeMapper.toRecipeEntity(recipe)
        recipeDao.insert(recipeEntity)
        recipe.ingredients.forEach { ingredient ->
            val ingredientEntity = DbIngredientMapper.toIngredientEntity(ingredient)
            ingredientDao.insert(ingredientEntity)
        }
    }

    override suspend fun getMyRecipes(): List<Recipe> {
        val recipesWithIngredient = recipeDao.getAllRecipesWithIngredients()
        val list = recipesWithIngredient.first()
        return list.map { recipeWithIngredients ->
            DbRecipeMapper.toRecipe(recipeWithIngredients)
        }
    }

    override suspend fun getRecipeById(recipeId: String): Recipe {
        val recipeWithIngredient = recipeDao.getRecipeWithIngredientsById(recipeId).first()
        return DbRecipeMapper.toRecipe(recipeWithIngredient)
    }

    override suspend fun deleteRecipe(recipe: Recipe) {
        val recipeEntity = DbRecipeMapper.toRecipeEntity(recipe)
        recipeDao.delete(recipeEntity)
        ingredientDao.deleteIngredientsOfRecipe(recipe.id)
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        val recipeEntity = DbRecipeMapper.toRecipeEntity(recipe)
        recipeDao.update(recipeEntity)
    }

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

    override suspend fun uploadRecipe(recipe: Recipe): Result<Recipe> {
        val recipeDto = RecipeMapper.toDto(recipe)
        return try {
            val response = api.uploadRecipe(recipeDto)
            if (response.isSuccessful) {
                val uploadedRecipe = response.body()
                if (uploadedRecipe != null) {
                    Result.Success(RecipeMapper.toEntity(uploadedRecipe))
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
        }  catch (io: IOException) {
            Result.Error(message = "Bitte überprüfen Sie Ihre Internetverbindung.")
        }
    }

    override suspend fun reportRecipe(recipeReport: RecipeReport): Result<ReportDTO> {
        val recipeReportDto = ReportMapper.toDto(recipeReport)
        return try {
            val response = api.reportRecipe(recipeReportDto)
            if (response.isSuccessful) {
                val reportedRecipe = response.body()
                if (reportedRecipe != null) {
                    Result.Success(ReportMapper.toDto(recipeReport))
                } else {
                    Result.Error(message = "Leeres Rezept erhalten.")
                }
            } else {
                Result.Error(message = "Fehler beim Melden des Rezepts: ${response.errorBody()?.string()}")
            }
        } catch (io: IOException) {
            Result.Error(message = "Bitte überprüfen Sie Ihre Internetverbindung.")
        }
    }
}