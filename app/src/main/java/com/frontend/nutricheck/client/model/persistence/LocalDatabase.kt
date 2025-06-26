package com.frontend.nutricheck.client.model.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.frontend.nutricheck.client.model.data_layer.FoodProduct
import com.frontend.nutricheck.client.model.data_layer.HistoryDay
import com.frontend.nutricheck.client.model.data_layer.Meal
import com.frontend.nutricheck.client.model.data_layer.Recipe
import com.frontend.nutricheck.client.model.data_layer.UserData
import com.frontend.nutricheck.client.model.data_layer.Weight
import com.frontend.nutricheck.client.model.persistence.dao.FoodDao
import com.frontend.nutricheck.client.model.persistence.dao.HistoryDao
import com.frontend.nutricheck.client.model.persistence.dao.MealDao
import com.frontend.nutricheck.client.model.persistence.dao.RecipeDao
import com.frontend.nutricheck.client.model.persistence.dao.UserDataDao
import com.frontend.nutricheck.client.model.persistence.dao.WeightDao

@Database(
    entities = [
        FoodProduct::class,
        Recipe::class,
        Meal::class,
        HistoryDay::class,
        UserData::class,
        Weight::class
    ],
    version = 1,
    exportSchema = false
)


abstract class LocalDatabase : RoomDatabase() {

    //DAOs registrieren
    abstract fun foodDao(): FoodDao
    abstract fun recipeDao(): RecipeDao
    abstract fun mealDao(): MealDao
    abstract fun historyDao(): HistoryDao
    abstract fun weightDao(): WeightDao
    abstract fun userDataDao(): UserDataDao
}