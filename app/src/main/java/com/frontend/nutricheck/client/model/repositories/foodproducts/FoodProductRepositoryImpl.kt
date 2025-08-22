package com.frontend.nutricheck.client.model.repositories.foodproducts

import android.content.Context
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.dto.ErrorResponseDTO
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.FoodDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.search.FoodSearchDao
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.search.FoodSearchEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbFoodProductMapper
import com.frontend.nutricheck.client.model.data_sources.remote.RemoteApi
import com.frontend.nutricheck.client.model.repositories.mapper.FoodProductMapper
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Implementation of [FoodProductRepository] that handles searching and retrieving food products.
 */
class FoodProductRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val foodDao: FoodDao,
    private val foodSearchDao: FoodSearchDao,
    private val api: RemoteApi
) : FoodProductRepository {
    private val timeToLive = TimeUnit.MINUTES.toMillis(15)
    override suspend fun searchFoodProducts(foodProductName: String, language: String): Flow<Result<List<FoodProduct>>> = flow {
        val cached = foodSearchDao.resultsFor(foodProductName)
            .firstOrNull()
            ?.map { DbFoodProductMapper.toFoodProduct(it) }
            ?: emptyList()
        if (cached.isNotEmpty()) {
            emit(Result.Success(cached))
        }
            val lastUpdate = foodSearchDao.getLatestUpdatedFor(foodProductName)
            if (isExpired(lastUpdate)) {
                try {
                    val response = api.searchFoodProduct(foodProductName, language)
                    val body = response.body()
                    val errorBody = response.errorBody()
                    if (response.isSuccessful && body != null) {
                        val now = System.currentTimeMillis()
                        val foodProducts = body.map { FoodProductMapper.toData(it) }
                        val foodProductEntities =
                            foodProducts.map { DbFoodProductMapper.toFoodProductEntity(it) }
                        foodDao.insertAll(foodProductEntities)
                        foodSearchDao.clearQuery(foodProductName)
                        foodSearchDao.upsertEntities(foodProductEntities.map {
                            FoodSearchEntity(foodProductName, it.id, now)
                        })
                        emit(Result.Success(foodProducts))
                    } else if (errorBody != null) {
                        val errorResponse = Gson().fromJson(
                            errorBody.string(),
                            ErrorResponseDTO::class.java)
                        val code = errorResponse.body.status
                        val message = errorResponse.body.title + ": " + errorResponse.body.detail
                        emit(Result.Error(errorResponse.body.status, message))
                    } else {
                        emit(Result.Error(message = context.getString(R.string.unknown_error_message)))
                    }
                } catch (io: okio.IOException) {
                    emit(Result.Error(message = context.getString(R.string.io_exception_message)))
                }
            }
    }.flowOn(Dispatchers.IO)

    override suspend fun getFoodProductById(foodProductId: String): FoodProduct = withContext(Dispatchers.IO) {
        DbFoodProductMapper.toFoodProduct(foodDao.getById(foodProductId)) }

    private fun isExpired(lastUpdate: Long?): Boolean =
        lastUpdate == null || System.currentTimeMillis() - lastUpdate > timeToLive
}