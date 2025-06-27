package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Query
import com.frontend.nutricheck.client.model.data_sources.data.Recipe

interface RecipeDao : BaseDao<Recipe> {

    override suspend fun insert(obj: Recipe) {
        TODO("Not yet implemented")
    }

    override suspend fun update(obj: Recipe) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(obj: Recipe) {
        TODO("Not yet implemented")
    }

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getById(id: String): Recipe?

    @Query("SELECT * FROM recipes")
    suspend fun getAll(): List<Recipe>
}