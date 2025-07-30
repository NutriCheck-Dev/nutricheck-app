package com.frontend.nutricheck.client.model.repositories.history

import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.MealRecipeItem
import com.frontend.nutricheck.client.dto.ErrorResponseDTO
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.FoodDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.MealDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.MealFoodItemDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.MealRecipeItemDao
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealFoodItemEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealRecipeItemEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.RecipeEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbMealFoodItemMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbMealMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbMealRecipeItemMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealWithAll
import com.frontend.nutricheck.client.model.data_sources.remote.RemoteApi
import com.frontend.nutricheck.client.model.data_sources.remote.RetrofitInstance
import com.frontend.nutricheck.client.model.repositories.mapper.MealMapper
import com.google.gson.Gson
import okhttp3.MultipartBody
import java.io.IOException
import kotlinx.coroutines.flow.first
import java.util.Date
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val mealDao: MealDao,
    private val mealFoodItemDao: MealFoodItemDao,
    private val mealRecipeItemDao: MealRecipeItemDao,
    private val foodDao: FoodDao,
) : HistoryRepository {
    private val api = RetrofitInstance.getInstance().create(RemoteApi::class.java)

    override suspend fun getCaloriesOfDay(date: Date): Int {
        val meals = mealDao.getMealsWithAllForDay(date).map { DbMealMapper.toMeal(it) }
        return meals.sumOf { meal ->
            val foodItemsCalories = meal.mealFoodItems.sumOf { ingredient ->
                ingredient.quantity * ingredient.foodProduct.calories
            }

            val recipeItemsCalories = meal.mealRecipeItem.sumOf { recipe ->
                recipe.quantity * recipe.recipe.calories
            }
            foodItemsCalories + recipeItemsCalories
        }.toInt()
    }

  override suspend fun requestAiMeal(file: MultipartBody.Part): Result<Meal> {
        return try {
            val response = api.estimateMeal(file)
            val body = response.body()
            val errorBody = response.errorBody()

            if (response.isSuccessful && body != null) {
                Result.Success(MealMapper.toData(body))
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

    override suspend fun deleteMeal(meal: MealEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun updateMeal(meal: MealEntity) {
        //get meal by mealId, update the meal in the database

    }

    override suspend fun getMealsForDay(date: Date): List<MealWithAll> {
        return mealDao.getMealsWithAllForDay(date)
    }

    override suspend fun addFoodToMeal(name: String, foodId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun removeFoodFromMeal(name: String, foodId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun addMeal(
        meal: MealEntity,
        mealFoodItemsWithProduct: List<Pair<Double, FoodProductEntity>>?,
        mealRecipeItemsWithRecipeEntity: List<Pair<Double, RecipeEntity>>?
    ) {
        mealDao.insert(meal)
        val mealId = meal.id

        val mealFoodItems = mealFoodItemsWithProduct?.map { (quantity, foodProduct) ->
            MealFoodItemEntity(
                mealId = mealId,
                foodProductId = foodProduct.id,
                quantity = quantity
            )
        } ?: emptyList()

        val mealRecipeItems = mealRecipeItemsWithRecipeEntity?.map { (quantity, recipe) ->
            MealRecipeItemEntity(
                mealId = mealId,
                recipeId = recipe.id,
                quantity = quantity
            )
        } ?: emptyList()

        foodDao.insertAll(mealFoodItemsWithProduct?.map { it.second } ?: emptyList())

        mealFoodItemDao.insertAll(mealFoodItems)
        mealRecipeItemDao.insertAll(mealRecipeItems)
    }

    override suspend fun saveAsRecipe(
        meal: MealEntity,
        recipeName: String,
        recipeDescription: String
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getMealFoodItemById(
        mealId: String,
        foodProductId: String
    ): MealFoodItem {
        val mealFoodItemWithProduct = mealFoodItemDao.getItemOfMealById(mealId, foodProductId).first()
        return DbMealFoodItemMapper.toMealFoodItem(mealFoodItemWithProduct)
    }

    override suspend fun updateMealFoodItem(mealFoodItem: MealFoodItem) =
        mealFoodItemDao.update(DbMealFoodItemMapper.toMealFoodItemEntity(mealFoodItem))

    override suspend fun getMealRecipeItemById(
        mealId: String,
        recipeId: String
    ): MealRecipeItem {
        val mealRecipeItemWithRecipe = mealRecipeItemDao.getItemOfMealById(mealId, recipeId).first()
        return DbMealRecipeItemMapper.toMealRecipeItem(mealRecipeItemWithRecipe)
    }

    override suspend fun updateMealRecipeItem(mealRecipeItem: MealRecipeItem) =
        mealRecipeItemDao.update(DbMealRecipeItemMapper.toMealRecipeItemEntity(mealRecipeItem))
}