package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Query
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct

interface FoodDao : BaseDao<FoodProduct> {

    override suspend fun insert(obj: FoodProduct) {
        TODO("Not yet implemented")
    }

    override suspend fun update(obj: FoodProduct) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(obj: FoodProduct) {
        TODO("Not yet implemented")
    }

    @Query("SELECT * FROM foods WHERE id = :id")
    suspend fun getById(id: String): FoodProduct?

    @Query("SELECT * FROM foods")
    suspend fun getAll(): List<FoodProduct>

}