package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao : BaseDao<FoodProduct> {

    @Insert
    override suspend fun insert(obj: FoodProduct)

    @Update
    override suspend fun update(obj: FoodProduct)

    @Delete
    override suspend fun delete(obj: FoodProduct)

    @Query("SELECT * FROM foods WHERE id = :id")
    fun getById(id: String): Flow<FoodProduct>

    @Query("SELECT * FROM foods")
    fun getAll(): Flow<List<FoodProduct>>

}