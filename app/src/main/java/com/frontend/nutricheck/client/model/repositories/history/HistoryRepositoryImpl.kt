package com.frontend.nutricheck.client.model.repositories.history

import com.frontend.nutricheck.client.dto.ErrorResponseDTO
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
import com.frontend.nutricheck.client.model.repositories.mapper.MealMapper
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import java.io.IOException
import java.util.Date
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val mealDao: MealDao,
    private val mealFoodItemDao: MealFoodItemDao,
    private val mealRecipeItemDao: MealRecipeItemDao,
    private val foodDao: FoodDao,
    private val api: RemoteApi
) : HistoryRepository {

    override suspend fun getCaloriesOfDay(date: Date): Int = withContext(Dispatchers.IO) {
        val meals = mealDao.getMealsWithAllForDay(date).map { DbMealMapper.toMeal(it) }
        meals.sumOf { meal ->
            val foodItemsCalories = meal.mealFoodItems.sumOf { ingredient ->
                ingredient.quantity * ingredient.foodProduct.calories
            }

            val recipeItemsCalories = meal.mealRecipeItems.sumOf { recipe ->
                recipe.quantity * recipe.recipe.calories
            }
            foodItemsCalories + recipeItemsCalories
        }.toInt()
    }


    override suspend fun requestAiMeal(file: MultipartBody.Part): Result<Meal> = withContext(Dispatchers.IO) {
        try {
            val response = api.estimateMeal(file)
            val body = response.body()
            val errorBody = response.errorBody()

            if (response.isSuccessful && body != null) {
                val meal = MealMapper.toData(body)
                addMeal(meal)
                Result.Success(meal)
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

    override suspend fun deleteMeal(meal: Meal) = withContext(Dispatchers.IO) {
        mealDao.delete(DbMealMapper.toMealEntity(meal))
    }

    override suspend fun updateMeal(meal: Meal) = withContext(Dispatchers.IO) {

        mealDao.update(DbMealMapper.toMealEntity(meal))

        checkForFoodProducts(meal.mealFoodItems)
        val mealFoodItemsEntities =
            meal.mealFoodItems.map { DbMealFoodItemMapper.toMealFoodItemEntity(it) }
        mealFoodItemDao.deleteMealFoodItemsOfMeal(meal.id)
        mealFoodItemDao.insertAll(mealFoodItemsEntities)

        val mealRecipeItemEntities =
            meal.mealRecipeItems.map { DbMealRecipeItemMapper.toMealRecipeItemEntity(it) }
        mealRecipeItemDao.deleteMealRecipeItemsOfMeal(meal.id)
        mealRecipeItemDao.insertAll(mealRecipeItemEntities)
    }

    override suspend fun getMealsForDay(date: Date): List<Meal> = withContext(Dispatchers.IO) {
        mealDao.getMealsWithAllForDay(date).map { DbMealMapper.toMeal(it) }
    }

    override suspend fun getMealById(mealId: String): Meal = withContext(Dispatchers.IO) {
        DbMealMapper.toMeal(mealDao.getById(mealId))
    }

    override suspend fun addMeal(meal: Meal) = withContext(Dispatchers.IO) {
        //check if meal exists
        val mealEntities = mealDao.getMealsWithAllForDay(meal.date)
        mealEntities.forEach { mealEntity -> if (mealEntity.meal.id == meal.id) throw Exception() }//error duplicate meal//TODO


        val mealEntity = DbMealMapper.toMealEntity(meal)
        mealDao.insert(mealEntity)

        checkForFoodProducts(meal.mealFoodItems)
        mealFoodItemDao.insertAll(meal.mealFoodItems.map {
            DbMealFoodItemMapper.toMealFoodItemEntity(
                it
            )
        })

        val mealRecipeItemEntities =
            meal.mealRecipeItems.map { DbMealRecipeItemMapper.toMealRecipeItemEntity(it) }
        mealRecipeItemDao.insertAll(mealRecipeItemEntities)
    }

    override suspend fun getDailyMacros() : List<Int> {
        val meals = getMealsForDay(Date())
        val dailyProtein = meals.sumOf { it.protein }.toInt()
        val dailyCarbohydrates = meals.sumOf { it.carbohydrates }.toInt()
        val dailyFat = meals.sumOf { it.fat }.toInt()
        return listOf(dailyCarbohydrates, dailyProtein, dailyFat)
    }

    override suspend fun getMealFoodItemById(
        mealId: String,
        foodProductId: String
    ): MealFoodItem = withContext(Dispatchers.IO) {
        val mealFoodItemWithProduct = mealFoodItemDao.getItemOfMealById(mealId, foodProductId)
        DbMealFoodItemMapper.toMealFoodItem(mealFoodItemWithProduct)
    }

    override suspend fun updateMealFoodItem(mealFoodItem: MealFoodItem) =
        withContext(Dispatchers.IO) {
    mealFoodItemDao.update(DbMealFoodItemMapper.toMealFoodItemEntity(mealFoodItem))
    }

    override suspend fun getMealRecipeItemById(
        mealId: String,
        recipeId: String
    ): MealRecipeItem = withContext(Dispatchers.IO) {
        val mealRecipeItemWithRecipe = mealRecipeItemDao.getItemOfMealById(mealId)
        DbMealRecipeItemMapper.toMealRecipeItem(mealRecipeItemWithRecipe)
    }

    override suspend fun updateMealRecipeItem(mealRecipeItem: MealRecipeItem) = withContext(Dispatchers.IO) {
        mealRecipeItemDao.update(DbMealRecipeItemMapper.toMealRecipeItemEntity(mealRecipeItem))
    }

    private suspend fun checkForFoodProducts(mealFoodItems: List<MealFoodItem>) = withContext(Dispatchers.IO) {
        for (mealFoodItem in mealFoodItems) {
            if (!foodDao.exists(mealFoodItem.foodProduct.id)) {
                foodDao.insert(DbFoodProductMapper.toFoodProductEntity(mealFoodItem.foodProduct))
            }
        }
    }

    //TODO: Implement checkForRecipes
}