package com.nutricheck.frontend.model

import com.frontend.nutricheck.client.model.data_sources.remote.RemoteApi
import com.nutricheck.frontend.TestDataFactory
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.test.assertEquals

class RetrofitTest {
    lateinit var mockWebServer: MockWebServer
    lateinit var api: RemoteApi

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(RemoteApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `upload recipe`() = runTest {
        val recipeDTO = TestDataFactory.createDefaultRecipeDTO()
        val recipeJson = TestDataFactory.createDefaultRecipeJson()
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(recipeJson)

        mockWebServer.enqueue(response)
        val result = api.uploadRecipe(recipeDTO)

        assertEquals(200, result.code())
        assertEquals(recipeDTO, result.body())
    }

    @Test
    fun `report recipe`() = runTest {
        val reportDto = TestDataFactory.createDefaultReportDTO()
        val reportJson = TestDataFactory.createDefaultReportJson()
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(reportJson)

        mockWebServer.enqueue(response)
        val result = api.reportRecipe(reportDto)

        assertEquals(200, result.code())
        assertEquals(reportDto, result.body())
    }

    @Test
    fun `search food product`() = runTest {
        val foodProductDTO = TestDataFactory.createDefaultFoodProductDTO()
        val foodProductJson = TestDataFactory.createDefaultFoodProductListJson()
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(foodProductJson)

        mockWebServer.enqueue(response)
        val result = api.searchFoodProduct(foodProductDTO.name, "en")

        assertEquals(200, result.code())
        assertEquals(listOf(foodProductDTO, foodProductDTO), result.body())
    }

    @Test
    fun `search recipes`() = runTest {
        val recipeDTO = TestDataFactory.createDefaultRecipeDTO()
        val recipeJson = TestDataFactory.createDefaultRecipeJson()
        val response = MockResponse()
            .setResponseCode(200)
            .setBody("[$recipeJson]")

        mockWebServer.enqueue(response)
        val result = api.searchRecipes(recipeDTO.name)

        assertEquals(200, result.code())
        assertEquals(listOf(recipeDTO), result.body())
    }

    @Test
    fun `estimate meal`() = runTest {
        val mealDTO = TestDataFactory.createDefaultMealDTO()
        val mealJson = TestDataFactory.createDefaultMealJson()
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(mealJson)

        mockWebServer.enqueue(response)
        val file = TestDataFactory.createDefaultMultipartBody()
        val result = api.estimateMeal(file)

        assertEquals(200, result.code())
        assertEquals(mealDTO, result.body())
    }
}