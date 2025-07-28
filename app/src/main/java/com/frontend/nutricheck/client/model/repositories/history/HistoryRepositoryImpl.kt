package com.frontend.nutricheck.client.model.repositories.history

import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.HistoryDay
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Meal
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealRecipeItem
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.RecipeEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.FoodDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.HistoryDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.MealDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.MealFoodItemDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.MealRecipeItemDao
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealWithAll
import com.frontend.nutricheck.client.model.data_sources.remote.RemoteApi
import com.frontend.nutricheck.client.model.data_sources.remote.RetrofitInstance
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val mealDao: MealDao,
    private val mealFoodItemDao: MealFoodItemDao,
    private val mealRecipeItemDao: MealRecipeItemDao,
    private val foodDao: FoodDao,
    private val historyDao: HistoryDao
) : HistoryRepository {
    private val api = RetrofitInstance.getInstance().create(RemoteApi::class.java)

    override suspend fun getCalorieHistory(): List<HistoryDay> {
        TODO("Not yet implemented")
    }

    override suspend fun getCaloriesOfDay(date: Date): Int {
        val mealsWithAll = mealDao.getMealsWithAllForDay(date) // oder repository.getMealsWithAllForDay(date)
        return mealsWithAll.sumOf { mealWithAll ->
            // Alle FoodItems dieses Meals
            val foodItemsCalories = mealWithAll.mealFoodItems.sumOf { itemWithProduct ->
                itemWithProduct.mealFoodItem.quantity * itemWithProduct.foodProductEntity.calories
            }
            // Alle RecipeItems dieses Meals
            val recipeItemsCalories = mealWithAll.mealRecipeItems.sumOf { itemWithRecipe ->
                itemWithRecipe.mealRecipeItem.quantity * itemWithRecipe.recipeEntity.calories
            }
            foodItemsCalories + recipeItemsCalories
        }.toInt() // auf ganze Zahl runden
    }

    override suspend fun getDailyHistory(date: Date): HistoryDay {
        TODO("Not yet implemented")
    }

    override suspend fun requestAiMeal(): Meal {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMeal(meal: Meal) {
        TODO("Not yet implemented")
    }

    override suspend fun updateMeal(meal: Meal) {
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
    override suspend fun getHistoryByDate(date: Date): Flow<HistoryDay> = historyDao.getByDate(date)

    override suspend fun addMeal(
        meal: Meal,
        mealFoodItemsWithProduct: List<Pair<Double, FoodProductEntity>>?,
        mealRecipeItemsWithRecipeEntity: List<Pair<Double, RecipeEntity>>?
    ) {
        mealDao.insert(meal)
        val mealId = meal.id

        val mealFoodItems = mealFoodItemsWithProduct?.map { (quantity, foodProduct) ->
            MealFoodItem(
                id = UUID.randomUUID().toString(),
                mealId = mealId,
                foodProductId = foodProduct.id,
                quantity = quantity
            )
        } ?: emptyList()

        val mealRecipeItems = mealRecipeItemsWithRecipeEntity?.map { (quantity, recipe) ->
            MealRecipeItem(
                id = UUID.randomUUID().toString(),
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
        meal: Meal,
        recipeName: String,
        recipeDescription: String
    ) {
        TODO("Not yet implemented")
    }
}