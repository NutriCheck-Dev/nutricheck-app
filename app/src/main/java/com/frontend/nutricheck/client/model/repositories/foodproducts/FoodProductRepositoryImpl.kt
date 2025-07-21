package com.frontend.nutricheck.client.model.repositories.foodproducts

import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.FoodDao
import com.frontend.nutricheck.client.model.data_sources.remote.RemoteApi
import com.frontend.nutricheck.client.model.data_sources.remote.RetrofitInstance
import com.frontend.nutricheck.client.model.repositories.mapper.FoodProductMapper
import java.io.IOException
import javax.inject.Inject

class FoodProductRepositoryImpl @Inject constructor(
    val foodDao: FoodDao
) : FoodProductRepository {
    private val api = RetrofitInstance.getInstance().create(RemoteApi::class.java)

    override suspend fun searchFoodProduct(foodProductName: String): List<FoodProduct> {
        return try {
            val response = api.searchFoodProduct(foodProductName)
            if (response.isSuccessful) {
                response.body()
                    .orEmpty()
                    .map { dTO ->
                        FoodProductMapper.toEntity(dTO)
                    }
            } else {
                emptyList()
            }
        } catch (io: IOException) {
            emptyList()
        }
    }
}