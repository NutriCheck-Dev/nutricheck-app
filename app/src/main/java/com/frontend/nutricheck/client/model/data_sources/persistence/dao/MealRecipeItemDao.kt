package com.frontend.nutricheck.client.model.data_sources.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.MealRecipeItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MealRecipeItemDao : BaseDao<MealRecipeItem> {

    @Insert
    override suspend fun insert(obj: MealRecipeItem)

    @Update
    override suspend fun update(obj: MealRecipeItem)

    @Delete
    override suspend fun delete(obj: MealRecipeItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mealFoodItems: List<MealRecipeItem>)

}