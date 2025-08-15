package com.nutricheck.frontend.model.repositories

import com.frontend.nutricheck.client.dto.FoodProductDTO
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.FoodDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.search.FoodSearchDao
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity
import com.frontend.nutricheck.client.model.data_sources.remote.RemoteApi
import com.frontend.nutricheck.client.model.repositories.foodproducts.FoodProductRepositoryImpl
import com.frontend.nutricheck.client.model.repositories.mapper.FoodProductMapper
import com.nutricheck.frontend.TestDataFactory
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import kotlin.test.assertNotNull

class FoodProductRepositoryImplTest {

    private lateinit var repository: FoodProductRepositoryImpl
    val foodSearchDao: FoodSearchDao = mockk(relaxed = true)
    val foodDao: FoodDao = mockk(relaxed = true)
    val api: RemoteApi = mockk(relaxed = true)
    private lateinit var query: String
    private lateinit var foodProductDTO: FoodProductDTO
    private lateinit var foodProduct: FoodProduct
    private lateinit var foodProductEntity: FoodProductEntity


    @Before
    fun setUp() {
        repository = FoodProductRepositoryImpl(foodDao, foodSearchDao, api)
        this.query = "test"
        this.foodProductDTO = TestDataFactory.createDefaultFoodProductDTO()
        this.foodProduct = TestDataFactory.createDefaultFoodProduct()
        this.foodProductEntity = TestDataFactory.createDefaultFoodProductEntity()
    }

    //test: cache, api, error, exception?
    @Test
    fun `search in api`() = runTest {
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
            assertNotNull(result.data)
            Assert.assertEquals(foodProductDTO.id, result.data[0].id)
            Assert.assertEquals(foodProductDTO.name, result.data[0].name)
            //deprecated assertEquals(foodProductDTO.fat, result.data[0].fat)
        }
        unmockkObject(FoodProductMapper)
    }
}