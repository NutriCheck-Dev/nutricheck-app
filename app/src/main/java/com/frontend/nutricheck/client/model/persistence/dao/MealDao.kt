package com.frontend.nutricheck.client.model.persistence.dao

import androidx.room.Query
import com.frontend.nutricheck.client.model.data_layer.Meal

interface MealDao : BaseDao<Meal> {

    override suspend fun insert(obj: Meal) {
        TODO("Not yet implemented")
    }

    override suspend fun update(obj: Meal) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(obj: Meal) {
        TODO("Not yet implemented")
    }

    @Query("SELECT * FROM meals WHERE id = :id")
    suspend fun getById(id: Long): Meal?
}