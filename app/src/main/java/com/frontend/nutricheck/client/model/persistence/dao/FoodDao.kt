package com.frontend.nutricheck.client.model.persistence.dao

import androidx.room.Query
import com.frontend.nutricheck.client.model.data_layer.Food

interface FoodDao : BaseDao<Food> {

    override suspend fun insert(obj: Food) {
        TODO("Not yet implemented")
    }

    override suspend fun update(obj: Food) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(obj: Food) {
        TODO("Not yet implemented")
    }

    @Query("SELECT * FROM foods WHERE id = :id")
    suspend fun getById(id: String): Food?

    @Query("SELECT * FROM foods")
    suspend fun getAll(): List<Food>

}