package com.frontend.nutricheck.client.model.repositories.foodproducts

import android.content.Context
import com.frontend.nutricheck.client.dto.FoodProductDTO
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.FoodDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.search.FoodSearchDao
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbFoodProductMapper
import com.frontend.nutricheck.client.model.data_sources.remote.RemoteApi
import com.frontend.nutricheck.client.model.repositories.mapper.FoodProductMapper
import com.frontend.nutricheck.client.ui.view_model.TestDataFactory
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class FoodProductRepositoryImplTest {

    private lateinit var repository: FoodProductRepositoryImpl
    val foodSearchDao: FoodSearchDao = mockk(relaxed = true)
    val foodDao: FoodDao = mockk(relaxed = true)
    val api: RemoteApi = mockk(relaxed = true)
    var context = mockk<Context>(relaxed = true)
    private lateinit var query: String
    private lateinit var foodProductDTO: FoodProductDTO
    private lateinit var foodProduct: FoodProduct
    private lateinit var foodProductEntity: FoodProductEntity
    private val testDispatcher = UnconfinedTestDispatcher()


    @Before
    fun setUp() {
        repository = FoodProductRepositoryImpl(context, foodDao, foodSearchDao, api, testDispatcher)
        this.query = "test"
        this.foodProductDTO = TestDataFactory.createDefaultFoodProductDTO()
        this.foodProduct = TestDataFactory.createDefaultFoodProduct()
        this.foodProductEntity = TestDataFactory.createDefaultFoodProductEntity()
    }

    @Test
    fun `search in api with empty cache`() = runTest {
        mockkObject(FoodProductMapper)

        every { foodSearchDao.resultsFor(query) } returns flowOf(listOf())
        coEvery { foodSearchDao.getLatestUpdatedFor(query) } returns null
        coEvery { api.searchFoodProduct(query, "en") } returns Response.success(
            listOf(
                foodProductDTO
            )
        )
        every { FoodProductMapper.toData(foodProductDTO) } returns foodProduct

        val result = repository.searchFoodProducts(query, "en").first()
        assert(result is Result.Success)

        if (result is Result.Success) {
            Assert.assertNotNull(result.data)
            Assert.assertEquals(foodProductDTO.id, result.data[0].id)
            Assert.assertEquals(foodProductDTO.name, result.data[0].name)
            Assert.assertEquals(foodProduct.calories, result.data[0].calories, 0.0)
            Assert.assertEquals(foodProduct.carbohydrates, result.data[0].carbohydrates, 0.0)
            Assert.assertEquals(foodProduct.protein, result.data[0].protein, 0.0)
            Assert.assertEquals(foodProduct.fat, result.data[0].fat, 0.0)
        }
        unmockkObject(FoodProductMapper)
    }

    @Test
    fun `search in cache`() = runTest {
        mockkObject(DbFoodProductMapper)

        every { foodSearchDao.resultsFor(query) } returns flowOf(listOf(foodProductEntity))
        every { DbFoodProductMapper.toFoodProduct(foodProductEntity) } returns foodProduct
        coEvery { foodSearchDao.getLatestUpdatedFor(query) } returns System.currentTimeMillis()

        val result = repository.searchFoodProducts(query, "en").first()
        assert(result is Result.Success)

        if (result is Result.Success) {
            Assert.assertNotNull(result.data)
            Assert.assertEquals(foodProductEntity.id, result.data[0].id)
            Assert.assertEquals(foodProductEntity.name, result.data[0].name)
            Assert.assertEquals(foodProduct.calories, result.data[0].calories, 0.0)
            Assert.assertEquals(foodProduct.carbohydrates, result.data[0].carbohydrates, 0.0)
            Assert.assertEquals(foodProduct.protein, result.data[0].protein, 0.0)
            Assert.assertEquals(foodProduct.fat, result.data[0].fat, 0.0)
        }
        unmockkObject(DbFoodProductMapper)
    }

    @Test
    fun `search in api with error`() = runTest {
        every { foodSearchDao.resultsFor(query) } returns flowOf(listOf())
        coEvery { foodSearchDao.getLatestUpdatedFor(query) } returns null
        coEvery { api.searchFoodProduct(query, "en") } returns Response.error(
            400,
            TestDataFactory.createDefaultErrorMessage()
                .toResponseBody("application/json".toMediaTypeOrNull())
        )

        val result = repository.searchFoodProducts(query, "en").first()
        assert(result is Result.Error)

        if (result is Result.Error) {
            Assert.assertEquals(400, result.code)
            Assert.assertEquals("Bad Request: Invalid request content.", result.message)
        }
    }

    @Test
    fun `get food product by id`() = runTest {
        mockkObject(DbFoodProductMapper)
        val foodProductId = "testId"

        every { foodDao.getById(foodProductId) } returns foodProductEntity
        every { DbFoodProductMapper.toFoodProduct(foodProductEntity) } returns foodProduct

        val result = repository.getFoodProductById(foodProductId)
        Assert.assertEquals(foodProduct.id, result.id)
        Assert.assertEquals(foodProduct.name, result.name)
        Assert.assertEquals(foodProduct.calories, result.calories, 0.0)
        Assert.assertEquals(foodProduct.carbohydrates, result.carbohydrates, 0.0)
        Assert.assertEquals(foodProduct.protein, result.protein, 0.0)
        Assert.assertEquals(foodProduct.fat, result.fat, 0.0)
        unmockkObject(DbFoodProductMapper)
    }
}