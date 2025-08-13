package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity

/**
 * DAO for managing food product records in the database.
 */
@Dao
interface FoodDao : BaseDao<FoodProductEntity> {

    /**
     * Inserts a new food product.
     * @param obj The food product entity to insert
     */
    @Insert
    override suspend fun insert(obj: FoodProductEntity)

    /**
     * Updates an existing food product.
     * @param obj The food product entity to update
     */
    @Update
    override suspend fun update(obj: FoodProductEntity)

    /**
     * Deletes a food product.
     * @param obj The food product entity to delete
     */
    @Delete
    override suspend fun delete(obj: FoodProductEntity)

    /**
     * Inserts multiple food products, replacing existing ones.
     * @param foodProductEntities List of food products to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(foodProductEntities: List<FoodProductEntity>)

    /**
     * Gets a food product by its ID.
     * @param id The food product ID
     * @return The food product entity
     */
    @Query("SELECT * FROM foods WHERE id = :id")
    fun getById(id: String): FoodProductEntity

    /**
     * Checks if a food product exists.
     * @param id The food product ID to check
     * @return True if the food product exists
     */
    @Query("SELECT EXISTS(SELECT 1 FROM foods WHERE id = :id)")
    suspend fun exists(id: String): Boolean
}