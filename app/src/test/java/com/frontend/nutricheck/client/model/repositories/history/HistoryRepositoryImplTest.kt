package com.frontend.nutricheck.client.model.repositories.history

import android.content.Context
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.FoodDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.IngredientDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.MealDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.MealFoodItemDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.MealRecipeItemDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.RecipeDao
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbFoodProductMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbMealFoodItemMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbMealMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbMealRecipeItemMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbRecipeMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealWithAll
import com.frontend.nutricheck.client.model.data_sources.remote.RemoteApi
import com.frontend.nutricheck.client.model.repositories.mapper.MealMapper
import com.frontend.nutricheck.client.ui.view_model.TestDataFactory
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.unmockkObject
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions
import retrofit2.Response
import java.util.Date

class HistoryRepositoryImplTest {

    private lateinit var repository: HistoryRepositoryImpl
    val mealDao: MealDao = mockk(relaxed = true)
    val mealFoodItemDao: MealFoodItemDao = mockk(relaxed = true)
    val mealRecipeItemDao: MealRecipeItemDao = mockk(relaxed = true)
    val foodDao: FoodDao = mockk(relaxed = true)
    val recipeDao: RecipeDao = mockk(relaxed = true)
    val ingredientDao: IngredientDao = mockk(relaxed = true)
    val api: RemoteApi = mockk(relaxed = true)
    var context = mockk<Context>(relaxed = true)
    lateinit var meal: Meal
    lateinit var mealWithAll: MealWithAll

    @Before
    fun setUp() {
        repository = HistoryRepositoryImpl(
            context,
            mealDao,
            mealFoodItemDao,
            mealRecipeItemDao,
            foodDao,
            recipeDao,
            ingredientDao,
            api
        )
        this.meal = TestDataFactory.createDefaultMeal()
        this.mealWithAll = TestDataFactory.createDefaultMealWithAll()

        mockkObject(DbMealMapper)
        mockkObject(MealMapper)
        mockkObject(DbMealFoodItemMapper)
        mockkObject(DbMealRecipeItemMapper)
        mockkObject(DbRecipeMapper)
        mockkObject(DbFoodProductMapper)
    }

    @After
    fun tearDown() {
        unmockkObject(DbMealMapper)
        unmockkObject(MealMapper)
        unmockkObject(DbMealFoodItemMapper)
        unmockkObject(DbMealRecipeItemMapper)
        unmockkObject(DbRecipeMapper)
        unmockkObject(DbFoodProductMapper)
    }

    @Test
    fun `get calories of the day`() = runTest {
        every { DbMealMapper.toMeal(mealWithAll) } returns meal
        coEvery { mealDao.getMealsWithAllForDay(any()) } returns listOf(mealWithAll)

        val calories = repository.getCaloriesOfDay(Date())

        Assertions.assertEquals(
            meal.mealFoodItems.sumOf { it.quantity * it.foodProduct.calories } +
                    meal.mealRecipeItems.sumOf { it.quantity * it.recipe.calories }.toInt(),
            calories.toDouble()
        )
    }

    @Test
    fun `request AI meal`() = runTest {
        val file = mockk<MultipartBody.Part>()
        val mealDTO = TestDataFactory.createDefaultMealDTO()
        val languageCode = "en"

        coEvery { api.estimateMeal(file, languageCode) } returns Response.success(mealDTO)
        every { MealMapper.toData(mealDTO) } returns meal

        val result = repository.requestAiMeal(file, languageCode)

        if (result is Result.Success) {
            Assertions.assertEquals(meal, result.data)
        }
    }

    @Test
    fun `delete meal`() = runTest {
        val mealEntity = TestDataFactory.createDefaultMealEntity()
        every { DbMealMapper.toMealEntity(meal) } returns mealEntity
        coEvery { mealDao.delete(mealEntity) } just Runs

        repository.deleteMeal(meal)

        coVerify { mealDao.delete(mealEntity) }
    }

    @Test
    fun `update meal`() = runTest {
        val mealEntity = TestDataFactory.createDefaultMealEntity()
        val foodProductEntity = TestDataFactory.createDefaultFoodProductEntity()
        val recipeEntity = TestDataFactory.createDefaultRecipeEntity()
        val foodProductItemEntity = TestDataFactory.createDefaultMealFoodItemEntity()
        val recipeItemEntity = TestDataFactory.createDefaultMealRecipeItemEntity()
        every { DbMealMapper.toMealEntity(meal) } returns mealEntity
        coEvery { mealDao.update(mealEntity) } just Runs

        coEvery { foodDao.exists(meal.mealFoodItems.first().foodProduct.id) } returns false
        coEvery { foodDao.insert(foodProductEntity) } just Runs

        coEvery { recipeDao.insert(recipeEntity) } just Runs

        every { DbMealFoodItemMapper.toMealFoodItemEntity(meal.mealFoodItems.first()) } returns foodProductItemEntity
        coEvery { mealFoodItemDao.deleteMealFoodItemsOfMeal(meal.id) } just Runs
        coEvery { mealFoodItemDao.insertAll(listOf(foodProductItemEntity)) } just Runs

        every { DbMealRecipeItemMapper.toMealRecipeItemEntity(meal.mealRecipeItems.first()) } returns recipeItemEntity
        coEvery { mealRecipeItemDao.deleteMealRecipeItemsOfMeal(meal.id) } just Runs
        coEvery { mealRecipeItemDao.insertAll(listOf(recipeItemEntity)) } just Runs

        repository.updateMeal(meal)

        coVerify { mealDao.update(mealEntity) }
        coVerify { foodDao.insert(foodProductEntity) }
        coVerify { recipeDao.insert(any()) }
        coVerify { mealFoodItemDao.deleteMealFoodItemsOfMeal(meal.id) }
        coVerify { mealFoodItemDao.insertAll(listOf(foodProductItemEntity)) }
        coVerify { mealRecipeItemDao.deleteMealRecipeItemsOfMeal(meal.id) }
        coVerify { mealRecipeItemDao.insertAll(any()) }
    }

    @Test
    fun `get meals for day`() = runTest {
        val date = Date()
        coEvery { mealDao.getMealsWithAllForDay(date) } returns listOf(mealWithAll)
        every { DbMealMapper.toMeal(mealWithAll) } returns meal

        val meals = repository.getMealsForDay(date)

        Assertions.assertEquals(meal.id, meals.first().id)
        Assertions.assertEquals(meal.calories, meals.first().calories, 0.0)
    }

    @Test
    fun `remove meal item`() = runTest {
        val mealId = meal.id
        coEvery { mealDao.getById(mealId) } returns mealWithAll
        every { DbMealMapper.toMeal(mealWithAll) } returns meal
        coEvery { mealFoodItemDao.deleteItemOfMeal(mealId, meal.mealFoodItems.first().foodProduct.id) } just runs
        coEvery { mealRecipeItemDao.deleteItemOfMeal(mealId, meal.mealRecipeItems.first().recipe.id) } just runs

        repository.removeMealItem(meal.mealFoodItems.first())

        coVerify { mealFoodItemDao.deleteItemOfMeal(mealId, meal.mealFoodItems.first().foodProduct.id) }
    }

    @Test
    fun `get meal by id`() = runTest {
        val mealId = "testMealId"
        coEvery { mealDao.getById(mealId) } returns mealWithAll
        every { DbMealMapper.toMeal(mealWithAll) } returns meal

        val result = repository.getMealById(mealId)

        Assertions.assertEquals(meal.id, result.id)
        Assertions.assertEquals(meal.calories, result.calories, 0.0)
    }

    @Test
    fun `add meal`() = runTest {
        val mealEntity = TestDataFactory.createDefaultMealEntity()
        val foodProductItemEntity = TestDataFactory.createDefaultMealFoodItemEntity()
        val recipeItemEntity = TestDataFactory.createDefaultMealRecipeItemEntity()
        every { DbMealMapper.toMealEntity(meal) } returns mealEntity
        coEvery { mealDao.insert(mealEntity) } just Runs

        coEvery { foodDao.exists(meal.mealFoodItems.first().foodProduct.id) } returns false
        coEvery { foodDao.insert(any()) } just Runs

        coEvery { recipeDao.exists(meal.mealRecipeItems.first().recipe.id) } returns false
        coEvery { recipeDao.insert(any()) } just Runs

        every { DbMealFoodItemMapper.toMealFoodItemEntity(meal.mealFoodItems.first()) } returns foodProductItemEntity
        coEvery { mealFoodItemDao.insertAll(any()) } just Runs

        every { DbMealRecipeItemMapper.toMealRecipeItemEntity(meal.mealRecipeItems.first()) } returns recipeItemEntity
        coEvery { mealRecipeItemDao.insertAll(any()) } just Runs

        repository.addMeal(meal)

        coVerify { mealDao.insert(mealEntity) }
        coVerify { foodDao.insert(any()) }
        coVerify { recipeDao.insert(any()) }
        coVerify { mealFoodItemDao.insertAll(any()) }
        coVerify { mealRecipeItemDao.insertAll(any()) }
    }

    @Test
    fun `get daily macros`() = runTest {
        coEvery { mealDao.getMealsWithAllForDay(any()) } returns listOf(mealWithAll)
        every { DbMealMapper.toMeal(mealWithAll) } returns meal

        val macros = repository.getDailyMacros()

        Assertions.assertEquals(meal.carbohydrates.toInt(), macros[0])
        Assertions.assertEquals(meal.protein.toInt(), macros[1])
        Assertions.assertEquals(meal.fat.toInt(), macros[2])
    }

    @Test
    fun `get meal food Item by id`() = runTest {
        val mealId = "testMealId"
        val foodProductId = "testFoodProductId"
        val mealFoodItem = TestDataFactory.createDefaultFoodItemsWithProduct()
        val foodItem = TestDataFactory.createDefaultMealItem()
        coEvery { mealFoodItemDao.getItemOfMealById(mealId, foodProductId) } returns mealFoodItem
        every { DbMealFoodItemMapper.toMealFoodItem(mealFoodItem) } returns foodItem

        val result = repository.getMealFoodItemById(mealId, foodProductId)

        Assertions.assertEquals(mealId, result.mealId)
        Assertions.assertEquals(foodProductId, result.foodProduct.id)
    }

    @Test
    fun `update meal food item`() = runTest {
        val mealFoodItem = TestDataFactory.createDefaultMealItem()
        val mealFoodItemEntity = TestDataFactory.createDefaultMealFoodItemEntity()
        every { DbMealFoodItemMapper.toMealFoodItemEntity(mealFoodItem) } returns mealFoodItemEntity
        coEvery { mealFoodItemDao.update(mealFoodItemEntity) } just Runs

        repository.updateMealFoodItem(mealFoodItem)

        coVerify { mealFoodItemDao.update(mealFoodItemEntity) }
    }

    @Test
    fun `get meal recipe item by id`() = runTest {
        val mealId = "testMealId"
        val recipeId = "testRecipeId"
        val mealRecipeItem = TestDataFactory.createDefaultMealRecipeItem()
        val mealRecipeWithRecipe = TestDataFactory.createDefaultMealRecipeItemWithRecipe()
        coEvery { mealRecipeItemDao.getItemOfMealById(mealId) } returns mealRecipeWithRecipe
        every { DbMealRecipeItemMapper.toMealRecipeItem(mealRecipeWithRecipe) } returns mealRecipeItem

        val result = repository.getMealRecipeItemById(mealId, recipeId)

        Assertions.assertEquals(mealId, result.mealId)
        Assertions.assertEquals(recipeId, result.recipe.id)
    }

     @Test
    fun `update meal recipe item`() = runTest {
         val mealRecipeItem = TestDataFactory.createDefaultMealRecipeItem()
         val mealRecipeItemEntity = TestDataFactory.createDefaultMealRecipeItemEntity()
         every { DbMealRecipeItemMapper.toMealRecipeItemEntity(mealRecipeItem) } returns mealRecipeItemEntity
         coEvery { mealRecipeItemDao.update(mealRecipeItemEntity) } just Runs

         repository.updateMealRecipeItem(mealRecipeItem)

         coVerify { mealRecipeItemDao.update(mealRecipeItemEntity) }
     }

    @Test
    fun `observe meals for day`(): Unit = runTest {
        coEvery { mealDao.observeMealsForDay(any()) } returns
                flowOf(listOf(mealWithAll))
        every { DbMealMapper.toMeal(mealWithAll) } returns meal

        val flow = repository.observeMealsForDay(Date())

        flow.collect { meals ->
            Assertions.assertEquals(meal.id, meals.first().id)
            Assertions.assertEquals(meal.calories, meals.first().calories, 0.0)
        }
    }
}
