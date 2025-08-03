package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity

@Dao
interface FoodDao : BaseDao<FoodProductEntity> {

    @Insert
    override suspend fun insert(obj: FoodProductEntity)

    @Update
    override suspend fun update(obj: FoodProductEntity)

    @Delete
    override suspend fun delete(obj: FoodProductEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(foodProductEntities: List<FoodProductEntity>)

    @Query("SELECT * FROM foods WHERE id = :id")
    fun getById(id: String): FoodProductEntity

    @Query("SELECT EXISTS(SELECT 1 FROM foods WHERE id = :id)")
    suspend fun exists(id: String): Boolean
}