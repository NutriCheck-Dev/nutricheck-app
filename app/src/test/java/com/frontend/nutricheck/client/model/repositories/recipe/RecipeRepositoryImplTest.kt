package com.frontend.nutricheck.client.model.repositories.recipe

import android.content.Context
import com.frontend.nutricheck.client.dto.RecipeDTO
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.data.flags.RecipeVisibility
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.FoodDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.IngredientDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.RecipeDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.search.RecipeSearchDao
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbFoodProductMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbIngredientMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbRecipeMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.RecipeWithIngredients
import com.frontend.nutricheck.client.model.data_sources.remote.RemoteApi
import com.frontend.nutricheck.client.model.repositories.mapper.RecipeMapper
import com.frontend.nutricheck.client.model.repositories.mapper.ReportMapper
import com.frontend.nutricheck.client.ui.view_model.TestDataFactory
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import retrofit2.Response
import kotlin.test.Test

class RecipeRepositoryImplTest {

    private lateinit var repository: RecipeRepositoryImpl
    val recipeDao: RecipeDao = mockk(relaxed = true)
    val recipeSearchDao: RecipeSearchDao = mockk(relaxed = true)
    val ingredientDao: IngredientDao = mockk(relaxed = true)
    val foodDao: FoodDao = mockk(relaxed = true)
    val api: RemoteApi = mockk(relaxed = true)
    var context = mockk<Context>(relaxed = true)

    private lateinit var recipeDTO: RecipeDTO
    private lateinit var recipe: Recipe
    private lateinit var recipeWithIngredients: RecipeWithIngredients
    private lateinit var query: String

    @Before
    fun setUp() {
        repository = RecipeRepositoryImpl(context, recipeDao, recipeSearchDao, ingredientDao, foodDao, api)
        this.recipeDTO = TestDataFactory.createDefaultRecipeDTO()
        this.recipe = TestDataFactory.createDefaultRecipe()
        this.recipeWithIngredients = TestDataFactory.createDefaultRecipeWithIngredients()
        this.query = "test"

        mockkObject(DbRecipeMapper)
        mockkObject(RecipeMapper)
        mockkObject(ReportMapper)
        mockkObject(DbIngredientMapper)
        mockkObject(DbFoodProductMapper)
    }

    @After
    fun tearDown() {
        unmockkObject(DbRecipeMapper)
        unmockkObject(RecipeMapper)
        unmockkObject(ReportMapper)
        unmockkObject(DbIngredientMapper)
        unmockkObject(DbFoodProductMapper)
    }

    @Test
    fun `search recipes with cache`() = runTest {
        every { recipeSearchDao.resultsFor(query) } returns flowOf(listOf(recipeWithIngredients))
        every { DbRecipeMapper.toRecipe(recipeWithIngredients) } returns recipe
        coEvery { recipeSearchDao.getLatestUpdatedFor(query) } returns System.currentTimeMillis()

        val result = repository.searchRecipes(query).first()

        if (result is Result.Success) {
            compareRecipes(recipe, result.data.first())
        }
    }

    @Test
    fun `search recipes in api with empty cache`() = runTest {
        every { recipeSearchDao.resultsFor(query) } returns flowOf(listOf())
        coEvery { recipeSearchDao.getLatestUpdatedFor(query) } returns null
        coEvery { api.searchRecipes(query) } returns Response.success(
            listOf(recipeDTO)
        )
        every { RecipeMapper.toData(recipeDTO) } returns recipe

        val result = repository.searchRecipes(query).first()

        if (result is Result.Success) {
            compareRecipes(recipe, result.data.first())
        }
    }

    @Test
    fun `get my recipes`() = runTest {
        every { recipeDao.getAllRecipesWithIngredients(RecipeVisibility.OWNER) } returns flowOf(
            listOf(recipeWithIngredients)
        )
        every { DbRecipeMapper.toRecipe(recipeWithIngredients) } returns recipe

        val result = repository.getMyRecipes().first()
        compareRecipes(recipe, result)
    }

    @Test
    fun `insert recipe`() = runTest {
        val foodProductEntity = TestDataFactory.createDefaultFoodProductEntity()

        val recipeEntity = TestDataFactory.createDefaultRecipeEntity()
        val ingredientEntity = TestDataFactory.createDefaultIngredientEntity()
        every { DbRecipeMapper.toRecipeEntity(recipe, false) } returns recipeEntity
        coEvery { recipeDao.insert(recipeEntity) } just Runs
        every { DbIngredientMapper.toIngredientEntity(recipe.ingredients.first()) } returns ingredientEntity

        coEvery {foodDao.exists(recipe.ingredients.first().foodProduct.id)} returns false
        every { DbFoodProductMapper.toFoodProductEntity(recipe.ingredients.first().foodProduct) } returns foodProductEntity
        coEvery { foodDao.insert(foodProductEntity) } just Runs
        coEvery { ingredientDao.insert(ingredientEntity) } just Runs

        repository.insertRecipe(recipe)

        coVerify { recipeDao.insert(recipeEntity) }
        coVerify { foodDao.insert(foodProductEntity)}
        coVerify { ingredientDao.insert(ingredientEntity) }
    }

    @Test
    fun `observe my recipes`() = runTest {
        every { recipeDao.getAllRecipesWithIngredients(RecipeVisibility.OWNER) } returns flowOf(
            listOf(recipeWithIngredients)
        )
        every { DbRecipeMapper.toRecipe(recipeWithIngredients) } returns recipe

        val result = repository.observeMyRecipes().first()
        Assert.assertNotNull(result)
        compareRecipes(recipe, result.first())
    }

    @Test
    fun `get recipe by id`() = runTest {
        every { recipeDao.getRecipeWithIngredientsById(recipe.id) } returns flowOf(
            recipeWithIngredients
        )
        every { DbRecipeMapper.toRecipe(recipeWithIngredients) } returns recipe

        val result = repository.getRecipeById(recipe.id)
        compareRecipes(recipe, result)
    }

    @Test
    fun `upload recipe`() = runTest {
        coEvery { api.uploadRecipe(recipeDTO) } returns Response.success(recipeDTO)
        every { RecipeMapper.toDto(recipe) } returns recipeDTO

        val result = repository.uploadRecipe(recipe)

        if (result is Result.Success) {
            compareRecipes(recipe, result.data)
        }
    }

    @Test
    fun `delete recipe`() = runTest {
        val recipeEntity = TestDataFactory.createDefaultRecipeEntityDeleted()
        every { DbRecipeMapper.toRecipeEntity(recipe, true) } returns recipeEntity
        coEvery { recipeDao.update(recipeEntity) } just Runs

        repository.deleteRecipe(recipe)

        coVerify { recipeDao.update(recipeEntity) }
    }

    @Test
    fun `update recipe`() = runTest {
        val recipeEntity = TestDataFactory.createDefaultRecipeEntity()
        val foodProductEntity = TestDataFactory.createDefaultFoodProductEntity()
        every { DbRecipeMapper.toRecipeEntity(recipe, false) } returns recipeEntity
        coEvery { recipeDao.update(recipeEntity) } just Runs

        val ingredientEntity = TestDataFactory.createDefaultIngredientEntity()
        every { DbIngredientMapper.toIngredientEntity(recipe.ingredients.first()) } returns ingredientEntity
        coEvery { ingredientDao.deleteIngredientsOfRecipe(recipe.id) } just Runs
        coEvery { ingredientDao.insert(ingredientEntity) } just Runs
        coEvery { foodDao.exists(recipe.ingredients.first().foodProduct.id) } returns false
        every { DbFoodProductMapper.toFoodProductEntity(recipe.ingredients.first().foodProduct) } returns foodProductEntity
        coEvery { foodDao.insert(foodProductEntity) } just Runs

        repository.updateRecipe(recipe)

        coVerify { recipeDao.update(recipeEntity) }
        coVerify { ingredientDao.deleteIngredientsOfRecipe(recipe.id) }
        coVerify { ingredientDao.insert(ingredientEntity) }
        coVerify { foodDao.insert(foodProductEntity) }
    }

    @Test
    fun `report recipe`() = runTest {
        val report = TestDataFactory.createDefaultReport()
        val reportDTO = TestDataFactory.createDefaultReportDTO()

        coEvery { api.reportRecipe(reportDTO) } returns Response.success(reportDTO)
        every { ReportMapper.toDto(report) } returns reportDTO

        val result = repository.reportRecipe(report)
        if (result is Result.Success) {
            Assert.assertNotNull(result.data)
            Assert.assertEquals(report.description, result.data.description)
            Assert.assertEquals(report.recipeId, result.data.recipeId)
        }
    }

    @Test
    fun `get ingredient by id`() = runTest {
        val ingredientWithFoodProduct = TestDataFactory.createDefaultIngredientWithFoodProduct()
        val ingredient = TestDataFactory.createDefaultIngredient()
        coEvery {ingredientDao.getIngredientById(recipe.id,
            ingredientWithFoodProduct.ingredient.foodProductId)} returns ingredientWithFoodProduct
        every { DbIngredientMapper.toIngredient(ingredientWithFoodProduct) } returns ingredient

        val result = repository.getIngredientById(recipe.id, ingredientWithFoodProduct.ingredient.foodProductId)
        Assert.assertNotNull(result)
        Assert.assertEquals(ingredient.foodProduct.id, result.foodProduct.id)
        Assert.assertEquals(ingredient.foodProduct.name, result.foodProduct.name)
        Assert.assertEquals(ingredient.quantity, result.quantity, 0.0)
    }

    @Test
    fun `update ingredient`() = runTest {
        val ingredientEntity = TestDataFactory.createDefaultIngredientEntity()
        every { DbIngredientMapper.toIngredientEntity(recipe.ingredients.first()) } returns ingredientEntity
        coEvery { ingredientDao.update(ingredientEntity) } just Runs

        repository.updateIngredient(recipe.ingredients.first())

        coVerify { ingredientDao.update(ingredientEntity) }
    }

    @Test
    fun `observe recipe by id`() = runTest {
        every { recipeDao.getRecipeWithIngredientsById(recipe.id) } returns flowOf(recipeWithIngredients)
        every { DbRecipeMapper.toRecipe(recipeWithIngredients) } returns recipe

        val result = repository.observeRecipeById(recipe.id).first()
        Assert.assertNotNull(result)
        compareRecipes(recipe, result)
    }
    fun compareRecipes(expected: Recipe, actual: Recipe) {
        Assert.assertNotNull(actual)
        Assert.assertEquals(expected.id, actual.id)
        Assert.assertEquals(expected.name, actual.name)
        Assert.assertEquals(expected.instructions, actual.instructions)
        Assert.assertEquals(expected.servings, actual.servings, 0.0)
        Assert.assertEquals(expected.calories, actual.calories, 0.0)
        Assert.assertEquals(expected.carbohydrates, actual.carbohydrates, 0.0)
        Assert.assertEquals(expected.protein, actual.protein, 0.0)
        Assert.assertEquals(expected.fat, actual.fat, 0.0)
    }
}