package com.frontend.nutricheck.client.model.repositories.foodproducts

import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.FoodDao
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbFoodProductMapper
import com.frontend.nutricheck.client.model.data_sources.remote.RemoteApi
import com.frontend.nutricheck.client.model.data_sources.remote.RetrofitInstance
import com.frontend.nutricheck.client.model.repositories.mapper.FoodProductMapper
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class FoodProductRepositoryImpl @Inject constructor(
    val foodDao: FoodDao
) : FoodProductRepository {
    private val api = RetrofitInstance.getInstance().create(RemoteApi::class.java)

    override suspend fun searchFoodProduct(foodProductName: String, language: String): List<FoodProduct> {
        val response = api.searchFoodProduct(foodProductName, language)
        if (response.isSuccessful) {
            return response.body()?.map { FoodProductMapper.toEntity(it) } ?: emptyList()
        } else {
            val msg = when (response.code()) {
                400 -> "Ungültige Anfrage (400)"
                401 -> "Nicht autorisiert (401)"
                404 -> "Nicht gefunden (404)"
                500 -> "Serverfehler (500)"
                else -> "Unbekannter Fehler (${response.code()})"
            }
            throw Exception(msg)
        }
    }

    override suspend fun getFoodProductById(foodProductId: String): FoodProduct {
        val foodProductEntity = foodDao.getById(foodProductId).first()
        return DbFoodProductMapper.toFoodProduct(foodProductEntity)
    }
}