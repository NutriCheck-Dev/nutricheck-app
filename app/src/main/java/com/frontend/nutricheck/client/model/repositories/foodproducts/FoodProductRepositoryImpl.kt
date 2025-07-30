package com.frontend.nutricheck.client.model.repositories.foodproducts

import com.frontend.nutricheck.client.dto.ErrorResponseDTO
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.FoodDao
import com.frontend.nutricheck.client.model.data_sources.remote.RemoteApi
import com.frontend.nutricheck.client.model.data_sources.remote.RetrofitInstance
import com.frontend.nutricheck.client.model.repositories.mapper.FoodProductMapper
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbFoodProductMapper
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import java.io.IOException
import javax.inject.Inject

class FoodProductRepositoryImpl @Inject constructor(
    val foodDao: FoodDao
) : FoodProductRepository {
    private val api = RetrofitInstance.getInstance().create(RemoteApi::class.java)

    override suspend fun searchFoodProduct(foodProductName: String, language: String): Result<List<FoodProduct>> {
        return try {
            val response = api.searchFoodProduct(foodProductName, language)
            val body = response.body()
            val errorBody = response.errorBody()

            if (response.isSuccessful && body != null) {
                val foodProducts: List<FoodProduct> = body.map { FoodProductMapper.toEntity(it) }
                Result.Success(foodProducts)
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
            Result.Error(message = "Connection issue")
        }
    }

    override suspend fun getFoodProductById(foodProductId: String): FoodProduct {
        val foodProductEntity = foodDao.getById(foodProductId).first()
        return DbFoodProductMapper.toFoodProduct(foodProductEntity)
    }
}