package com.frontend.nutricheck.client.model.repositories.history

import android.content.Context
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.dto.ErrorResponseDTO
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.MealItem
import com.frontend.nutricheck.client.model.data_sources.data.MealRecipeItem
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.FoodDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.IngredientDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.MealDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.MealFoodItemDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.MealRecipeItemDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.RecipeDao
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbFoodProductMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbIngredientMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbMealFoodItemMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbMealMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbMealRecipeItemMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbRecipeMapper
import com.frontend.nutricheck.client.model.data_sources.remote.RemoteApi
import com.frontend.nutricheck.client.model.repositories.mapper.MealMapper
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import java.io.IOException
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mealDao: MealDao,
    private val mealFoodItemDao: MealFoodItemDao,
    private val mealRecipeItemDao: MealRecipeItemDao,
    private val foodDao: FoodDao,
    private val recipeDao: RecipeDao,
    private val ingredientDao: IngredientDao,
    private val api: RemoteApi
) : HistoryRepository {

    override suspend fun getCaloriesOfDay(date: Date): Int = withContext(Dispatchers.IO) {
        val meals = mealDao.getMealsWithAllForDay(date).map { DbMealMapper.toMeal(it) }
        meals.sumOf { meal ->
            val foodItemsCalories = meal.mealFoodItems.sumOf { ingredient ->
                ingredient.servings * ingredient.foodProduct.calories * (ingredient.servingSize.getAmount() / 100)
            }

            val recipeItemsCalories = meal.mealRecipeItems.sumOf { recipe ->
                recipe.quantity * recipe.recipe.calories
            }
            foodItemsCalories + recipeItemsCalories
        }.toInt()
    }


    override suspend fun requestAiMeal(file: MultipartBody.Part, language : String): Result<Meal> =
        withContext(Dispatchers.IO) {
        try {
            val response = api.estimateMeal(file, language)
            val body = response.body()
            val errorBody = response.errorBody()

            if (response.isSuccessful && body != null) {
                val meal = MealMapper.toData(body)
                Result.Success(meal)
            } else if (errorBody != null) {
                val errorResponse = Gson().fromJson(
                    errorBody.string(),
                    ErrorResponseDTO::class.java)
                val message = errorResponse.body.title + ": "+ errorResponse.body.detail
                Result.Error(errorResponse.body.status, message)
            } else {
                Result.Error(message = context.getString(R.string.unknown_error_message))
            }
        } catch (e: IOException) {
            Result.Error(message = context.getString(R.string.io_exception_message))
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
            meal.mealRecipeItems.map { DbMealRecipeItemMapper.toMealRecipeItemEntity(copyRecipe(it)) }
        mealRecipeItemDao.deleteMealRecipeItemsOfMeal(meal.id)
        mealRecipeItemDao.insertAll(mealRecipeItemEntities)
    }

    override suspend fun getMealsForDay(date: Date): List<Meal> = withContext(Dispatchers.IO) {
        mealDao.getMealsWithAllForDay(date).map { DbMealMapper.toMeal(it) }
    }

    override suspend fun removeMealItem(mealItem: MealItem) = withContext(Dispatchers.IO) {
        val mealId = mealItem.mealId
        val mealWithAll = mealDao.getById(mealId)
        val meal = DbMealMapper.toMeal(mealWithAll)
        val itemCount = meal.mealFoodItems.size + meal.mealRecipeItems.size

        if (itemCount <= 1) {
            mealDao.deleteById(mealId)
        } else {
            when (mealItem) {
                is MealFoodItem -> mealFoodItemDao.deleteItemOfMeal(mealItem.mealId, mealItem.foodProduct.id)
                is MealRecipeItem -> mealRecipeItemDao.deleteItemOfMeal(mealItem.mealId, mealItem.recipe.id)
            }
        }
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
            meal.mealRecipeItems.map { DbMealRecipeItemMapper.toMealRecipeItemEntity(copyRecipe(it)) }
        mealRecipeItemDao.insertAll(mealRecipeItemEntities)
    }

    override suspend fun getDailyMacros() : List<Int> {
        val meals = getMealsForDay(Date())
        val dailyProtein = meals.sumOf { it.protein }.toInt()
        val dailyCarbohydrates = meals.sumOf { it.carbohydrates }.toInt()
        val dailyFat = meals.sumOf { it.fat }.toInt()
        return listOf(dailyCarbohydrates, dailyProtein, dailyFat)
    }

    //Necessary?
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

    override suspend fun observeMealsForDay(date: Date): Flow<List<Meal>> =
         mealDao.observeMealsForDay(date)
            .map { list ->
                list.map(DbMealMapper:: toMeal) }
            .flowOn(Dispatchers.IO)


    override suspend fun observeCaloriesOfDay(date: Date): Flow<Int> =
        mealDao.observeMealsForDay(date) // Flow<List<DbMealWithAll>>
            .map { dbMeals ->
                val meals = dbMeals.map { DbMealMapper.toMeal(it) }
                meals.sumOf { meal ->
                    val foodItemsCalories = meal.mealFoodItems.sumOf { ingredient ->
                        ingredient.servings * ingredient.foodProduct.calories * (ingredient.servingSize.getAmount() / 100)
                    }
                    val recipeItemsCalories = meal.mealRecipeItems.sumOf { recipe ->
                        recipe.quantity * recipe.recipe.calories
                    }
                    foodItemsCalories + recipeItemsCalories
                }.toInt()
            }
            .flowOn(Dispatchers.IO)


    private suspend fun checkForFoodProducts(mealFoodItems: List<MealFoodItem>) = withContext(Dispatchers.IO) {
        for (mealFoodItem in mealFoodItems) {
            if (!foodDao.exists(mealFoodItem.foodProduct.id)) {
                foodDao.insert(DbFoodProductMapper.toFoodProductEntity(mealFoodItem.foodProduct))
            }
        }
    }

    private suspend fun checkForFoodProducts(ingredient: Ingredient) = withContext(Dispatchers.IO) {
        if (!foodDao.exists(ingredient.foodProduct.id)) {
            foodDao.insert(DbFoodProductMapper.toFoodProductEntity(ingredient.foodProduct))
        }
    }

    private suspend fun copyRecipe(mealRecipeItem: MealRecipeItem) : MealRecipeItem = withContext(Dispatchers.IO) {
        val recipe = mealRecipeItem.recipe
        val id = UUID.randomUUID().toString()
        val copiedRecipe = recipe.copy(
            id = id,
            ingredients = recipe.ingredients.map { it.copy(recipeId = id) }
        )
        val copiedRecipeEntity = DbRecipeMapper.toRecipeEntity(copiedRecipe, true)
        recipeDao.insert(copiedRecipeEntity)

        copiedRecipe.ingredients.forEach { ingredient ->
            val ingredientEntity = DbIngredientMapper.toIngredientEntity(ingredient)
            checkForFoodProducts(ingredient)
            ingredientDao.insert(ingredientEntity)
        }
        mealRecipeItem.copy(
            recipe = copiedRecipe
        )
    }
}