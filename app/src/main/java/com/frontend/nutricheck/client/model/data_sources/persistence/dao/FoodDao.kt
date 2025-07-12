package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Query
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import kotlinx.coroutines.flow.Flow

interface FoodDao : BaseDao<FoodProduct> {

    override suspend fun insert(obj: FoodProduct)

    override suspend fun update(obj: FoodProduct)

    override suspend fun delete(obj: FoodProduct)

    @Query("SELECT * FROM foods WHERE id = :id")
    suspend fun getById(id: String): Flow<FoodProduct>

    @Query("SELECT * FROM foods")
    suspend fun getAll(): Flow<List<FoodProduct>>

}