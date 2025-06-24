package com.frontend.nutricheck.client.model.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.frontend.nutricheck.client.model.data_layer.Food
import com.frontend.nutricheck.client.model.data_layer.History
import com.frontend.nutricheck.client.model.data_layer.Meal
import com.frontend.nutricheck.client.model.data_layer.Recipe
import com.frontend.nutricheck.client.model.data_layer.UserData
import com.frontend.nutricheck.client.model.data_layer.Weight
import com.frontend.nutricheck.client.model.persistence.converter.FoodComponentConverter
import com.frontend.nutricheck.client.model.persistence.converter.HistoryConverter
import com.frontend.nutricheck.client.model.persistence.converter.MealConverter
import com.frontend.nutricheck.client.model.persistence.converter.UserDataConverter
import com.frontend.nutricheck.client.model.persistence.converter.WeightConverter
import com.frontend.nutricheck.client.model.persistence.dao.FoodDao
import com.frontend.nutricheck.client.model.persistence.dao.HistoryDao
import com.frontend.nutricheck.client.model.persistence.dao.MealDao
import com.frontend.nutricheck.client.model.persistence.dao.RecipeDao
import com.frontend.nutricheck.client.model.persistence.dao.UserDataDao
import com.frontend.nutricheck.client.model.persistence.dao.WeightDao

@Database(
    entities = [
        Food::class,
        Recipe::class,
        Meal::class,
        History::class,
        UserData::class,
        Weight::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    FoodComponentConverter::class,
    HistoryConverter::class,
    MealConverter::class,
    UserDataConverter::class,
    WeightConverter::class
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