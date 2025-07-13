package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeDao : BaseDao<Recipe> {
    @Insert
    override suspend fun insert(obj: Recipe)

    @Update
    override suspend fun update(obj: Recipe)
    @Delete
    override suspend fun delete(obj: Recipe)

    @Query("SELECT * FROM recipes WHERE id = :id")
    fun getById(id: String): Flow<Recipe>

    @Query("SELECT * FROM recipes ORDER BY name ASC")
    fun getAll(): Flow<List<Recipe>>
}