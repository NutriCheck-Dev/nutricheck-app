package com.frontend.nutricheck.client.model.repositories.history

import com.frontend.nutricheck.client.dto.ErrorResponseDTO
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.MealRecipeItem
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.FoodDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.MealDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.MealFoodItemDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.MealRecipeItemDao
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbFoodProductMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbMealFoodItemMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbMealMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbMealRecipeItemMapper
import com.frontend.nutricheck.client.model.data_sources.remote.RemoteApi
import com.frontend.nutricheck.client.model.data_sources.remote.RetrofitInstance
import com.frontend.nutricheck.client.model.repositories.mapper.MealMapper
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import java.io.IOException
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

    override suspend fun deleteMeal(meal: Meal) {
        mealDao.delete(DbMealMapper.toMealEntity(meal))
    }

    override suspend fun updateMeal(meal: Meal) {

        mealDao.update(DbMealMapper.toMealEntity(meal))

        val mealFoodItemsEntities = meal.mealFoodItems.map { DbMealFoodItemMapper.toMealFoodItemEntity(it) }
        mealFoodItemDao.deleteMealFoodItemsOfMeal(meal.id)
        mealFoodItemDao.insertAll(mealFoodItemsEntities)

        val mealRecipeItemEntities = meal.mealRecipeItem.map { DbMealRecipeItemMapper.toMealRecipeItemEntity(it) }
        mealRecipeItemDao.deleteMealRecipeItemsOfMeal(meal.id)
        mealRecipeItemDao.insertAll(mealRecipeItemEntities)
    }

    override suspend fun getMealsForDay(date: Date): List<Meal> {
        return mealDao.getMealsWithAllForDay(date).map { DbMealMapper.toMeal(it) }
    }

    override suspend fun addMeal(meal: Meal) {
        //check if meal exists
        val mealEntities = mealDao.getMealsWithAllForDay(meal.date)
        mealEntities.forEach { mealEntity -> if (mealEntity.meal.id.equals(meal.id)) throw Exception() }//error duplicate meal

        //check if foodProduct exists
        val foodProducts : List<FoodProduct> = meal.mealFoodItems.map { it.foodProduct }
        for (foodProduct in foodProducts) {
            if (!foodDao.exists(foodProduct.id)) {
                foodDao.insert(DbFoodProductMapper.toFoodProductEntity(foodProduct))
            }
        }

        val mealEntity = DbMealMapper.toMealEntity(meal)
        mealDao.insert(mealEntity)

        val mealFoodItemsEntities = meal.mealFoodItems.map { DbMealFoodItemMapper.toMealFoodItemEntity(it) }
        mealFoodItemDao.insertAll(mealFoodItemsEntities)

        val mealRecipeItemEntities = meal.mealRecipeItem.map { DbMealRecipeItemMapper.toMealRecipeItemEntity(it) }
        mealRecipeItemDao.insertAll(mealRecipeItemEntities)
    }

    override suspend fun getDailyMacros() {
        TODO("Not yet implemented")
    }

    override suspend fun getMealFoodItemById(
        mealId: String,
        foodProductId: String
    ): MealFoodItem {
        val mealFoodItemWithProduct = mealFoodItemDao.getItemOfMealById(mealId, foodProductId)
        return DbMealFoodItemMapper.toMealFoodItem(mealFoodItemWithProduct)
    }

    override suspend fun updateMealFoodItem(mealFoodItem: MealFoodItem) =
        mealFoodItemDao.update(DbMealFoodItemMapper.toMealFoodItemEntity(mealFoodItem))

    override suspend fun getMealRecipeItemById(
        mealId: String,
        recipeId: String
    ): MealRecipeItem {
        val mealRecipeItemWithRecipe = mealRecipeItemDao.getItemOfMealById(mealId)
        return DbMealRecipeItemMapper.toMealRecipeItem(mealRecipeItemWithRecipe)
    }

    override suspend fun updateMealRecipeItem(mealRecipeItem: MealRecipeItem) =
        mealRecipeItemDao.update(DbMealRecipeItemMapper.toMealRecipeItemEntity(mealRecipeItem))
}