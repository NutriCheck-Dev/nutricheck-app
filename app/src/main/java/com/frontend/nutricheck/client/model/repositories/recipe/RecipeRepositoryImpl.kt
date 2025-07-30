package com.frontend.nutricheck.client.model.repositories.recipe

import com.frontend.nutricheck.client.dto.ErrorResponseDTO
import com.frontend.nutricheck.client.dto.ReportDTO
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.RecipeReport
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.IngredientDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.MealDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.MealFoodItemDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.MealRecipeItemDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.RecipeDao
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbIngredientMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbMealFoodItemMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbMealMapper
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
    private val mealDao: MealDao,
    private val mealRecipeItemDao: MealRecipeItemDao,
    private val mealFoodItemDao: MealFoodItemDao,
    private var remoteRecipes: List<Recipe>
) : RecipeRepository {
    private val api = RetrofitInstance.getInstance().create(RemoteApi::class.java)

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
        for (recipe in remoteRecipes) {
            if (recipe.id == recipeId) {
                return recipe
            }
        }
        val recipeWithIngredients = recipeDao.getRecipeWithIngredientsById(recipeId).first()
        return DbRecipeMapper.toRecipe(recipeWithIngredients)
    }

    override suspend fun deleteRecipe(recipe: Recipe) {

        val recipeEntity = DbRecipeMapper.toRecipeEntity(recipe)
        val mealIdsWithRecipe = mealRecipeItemDao.getById(recipe.id)?.map { it.mealId }

        recipeDao.delete(recipeEntity) //deletes ingredients and mealRecipeItems

        //check if recipe in mealRecipeItem
        if (mealIdsWithRecipe != null) {
            for (mealId in mealIdsWithRecipe) {
                val meal = DbMealMapper.toMeal(mealDao.getById(mealId))
                val mealFoodItemsFromIngredients = recipe.ingredients.map { MealFoodItem(
                    mealId = mealId,
                    foodProduct = it.foodProduct,
                    quantity = it.quantity
                ) }
                val totalMealFoodItems = meal.mealFoodItems + mealFoodItemsFromIngredients
                //check for same foodProduct?/ How to handle different quantity of foodProduct to recipe??
                totalMealFoodItems.map {mealFoodItemDao.insert(DbMealFoodItemMapper.toMealFoodItemEntity(it))}
            }
        }
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        val recipeEntity = DbRecipeMapper.toRecipeEntity(recipe)
        val ingredientsEntity = recipe.ingredients.map { DbIngredientMapper.toIngredientEntity(it) }
        recipeDao.update(recipeEntity)
        ingredientDao.deleteIngredientsOfRecipe(recipeEntity.id)
        ingredientsEntity.forEach { ingredientDao.insert(it) }
    }

    override suspend fun searchRecipe(recipeName: String): Result<List<Recipe>> {
        return try {
            val response = api.searchRecipes(recipeName)
            val body = response.body()
            val errorBody = response.errorBody()

            if (response.isSuccessful && body != null) {
                val recipes: List<Recipe> = body.map { RecipeMapper.toData(it) }
                this.remoteRecipes = recipes
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

    override suspend fun uploadRecipe(recipe: Recipe): Result<Recipe> {
        return try {
            val response = api.uploadRecipe(RecipeMapper.toDto(recipe))
            val body = response.body()
            val errorBody = response.errorBody()

            if (response.isSuccessful && body != null) {
                Result.Success(RecipeMapper.toData(body))
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
}