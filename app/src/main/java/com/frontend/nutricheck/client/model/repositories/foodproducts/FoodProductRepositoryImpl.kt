package com.frontend.nutricheck.client.model.repositories.foodproducts

import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.FoodDao
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbFoodProductMapper
import com.frontend.nutricheck.client.model.data_sources.remote.RemoteApi
import com.frontend.nutricheck.client.model.data_sources.remote.RetrofitInstance
import com.frontend.nutricheck.client.model.repositories.mapper.FoodProductMapper
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.search.FoodSearchDao
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.search.FoodSearchEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FoodProductRepositoryImpl @Inject constructor(
    val foodDao: FoodDao,
    val foodSearchDao: FoodSearchDao,
    var remoteFoodProducts: List<FoodProduct>
) : FoodProductRepository {
    private val api = RetrofitInstance.getInstance().create(RemoteApi::class.java)
    private val timeToLive = TimeUnit.MINUTES.toMillis(30)

    override suspend fun searchFoodProducts(foodProductName: String, language: String): Flow<Result<List<FoodProduct>>> = flow {
        val cached = foodSearchDao.resultsFor(foodProductName)
            .firstOrNull()
            ?.map { DbFoodProductMapper.toFoodProduct(it) }
            ?: emptyList()
        emit(Result.Success(cached))

        val lastUpdate = foodSearchDao.getLatestUpdatedFor(foodProductName)
        if (isExpired(lastUpdate)) {
            val networkResult = try {
                val response = api.searchFoodProduct(foodProductName, language)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    val now = System.currentTimeMillis()
                    val foodProducts = body.map { FoodProductMapper.toData(it) }
                    val foodProductEntities = foodProducts.map { DbFoodProductMapper.toFoodProductEntity(it) }
                    foodDao.insertAll(foodProductEntities)
                    foodSearchDao.clearQuery(foodProductName)
                    foodSearchDao.upsertEntities(foodProductEntities.map {
                        FoodSearchEntity(foodProductName, it.id, now)
                    })
                    Result.Success(foodProducts)
                } else {
                    val message = response.errorBody()!!.string()
                    Result.Error(code = response.code(), message = message)
                }
            } catch (io: okio.IOException) {
                Result.Error(message = "Oops, an error has occurred. Please check your internet connection.")
            }
            emit(networkResult)
        }
    }

    override suspend fun getFoodProductById(foodProductId: String): FoodProduct {
        for (remoteFoodProduct in remoteFoodProducts) {
            if (remoteFoodProduct.id == foodProductId) {
                return remoteFoodProduct
            }
        }
        val foodProductEntity = foodDao.getById(foodProductId).first()
        return DbFoodProductMapper.toFoodProduct(foodProductEntity)
    }

    private fun isExpired(lastUpdate: Long?): Boolean =
        lastUpdate == null || System.currentTimeMillis() - lastUpdate > timeToLive
}