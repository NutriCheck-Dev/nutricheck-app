package com.frontend.nutricheck.client.model.repositories.history

import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.MealRecipeItem
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.HistoryDay
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealFoodItemEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealRecipeItemEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.RecipeEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.FoodDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.HistoryDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.MealDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.MealFoodItemDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.MealRecipeItemDao
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbMealFoodItemMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbMealMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbMealRecipeItemMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealWithAll
import com.frontend.nutricheck.client.model.data_sources.remote.RemoteApi
import com.frontend.nutricheck.client.model.data_sources.remote.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    override suspend fun getDailyHistory(date: Date): HistoryDay {
        TODO("Not yet implemented")
    }

    override suspend fun requestAiMeal(): MealEntity {
        TODO("Not yet implemented")
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
    override suspend fun getHistoryByDate(date: Date): Flow<HistoryDay> = historyDao.getByDate(date)

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