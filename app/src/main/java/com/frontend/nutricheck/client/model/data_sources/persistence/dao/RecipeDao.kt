package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Query
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeDao : BaseDao<Recipe> {

    override suspend fun insert(obj: Recipe)

    override suspend fun update(obj: Recipe)

    override suspend fun delete(obj: Recipe)

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getById(id: String): Flow<Recipe>

    @Query("SELECT * FROM recipes ORDER BY name ASC")
    suspend fun getAll(): Flow<List<Recipe>>
}