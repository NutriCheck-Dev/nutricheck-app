package com.nutricheck.frontend

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import com.frontend.nutricheck.client.model.data_sources.persistence.DatabaseModule
import com.frontend.nutricheck.client.model.data_sources.persistence.LocalDatabase
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.FoodDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.IngredientDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.MealDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.MealFoodItemDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.MealRecipeItemDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.RecipeDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.UserDataDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.WeightDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.search.FoodSearchDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.search.RecipeSearchDao
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import java.io.File
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object TestDatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) : LocalDatabase =
        Room.inMemoryDatabaseBuilder(context, LocalDatabase::class.java)
            .allowMainThreadQueries()
            .build()

    @Singleton
    @Provides
    fun provideFoodDao(database: LocalDatabase): FoodDao = database.foodDao()

    @Singleton
    @Provides
    fun provideRecipeDao(database: LocalDatabase): RecipeDao = database.recipeDao()

    @Singleton
    @Provides
    fun provideWeightDao(database: LocalDatabase): WeightDao = database.weightDao()

    @Singleton
    @Provides
    fun provideUserDataDao(database: LocalDatabase): UserDataDao = database.userDataDao()

    @Singleton
    @Provides
    fun provideIngredientDao(database: LocalDatabase): IngredientDao = database.ingredientDao()

    @Singleton
    @Provides
    fun provideMealDao(database: LocalDatabase): MealDao = database.mealDao()

    @Singleton
    @Provides
    fun provideMealFoodItemDao(database: LocalDatabase): MealFoodItemDao = database.mealFoodItemDao()

    @Singleton
    @Provides
    fun provideMealRecipeItemDao(database: LocalDatabase): MealRecipeItemDao = database.mealRecipeItemDao()

    @Singleton
    @Provides
    fun provideRecipeSearchDao(database: LocalDatabase): RecipeSearchDao = database.recipeSearchDao()

    @Singleton
    @Provides
    fun provideFoodSearchDao(database: LocalDatabase): FoodSearchDao = database.foodSearchDao()
}