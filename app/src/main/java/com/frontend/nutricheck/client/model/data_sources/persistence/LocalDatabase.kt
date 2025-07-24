package com.frontend.nutricheck.client.model.data_sources.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.UserData
import com.frontend.nutricheck.client.model.data_sources.data.HistoryDay
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.MealRecipeItem
import com.frontend.nutricheck.client.model.data_sources.data.Weight
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.FoodDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.HistoryDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.RecipeDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.UserDataDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.WeightDao

@Database(
    entities = [
        HistoryDay::class,
        Ingredient::class,
        Meal::class,
        MealFoodItem::class,
        MealRecipeItem::class,
        FoodProduct::class,
        Recipe::class,
        UserData::class,
        Weight::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {
    //DAOs registrieren
    abstract fun foodDao(): FoodDao
    abstract fun recipeDao(): RecipeDao
    abstract fun historyDao(): HistoryDao
    abstract fun weightDao(): WeightDao
    abstract fun userDataDao(): UserDataDao
}