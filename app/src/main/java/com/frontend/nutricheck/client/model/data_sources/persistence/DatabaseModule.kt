package com.frontend.nutricheck.client.model.data_sources.persistence

import android.content.Context
import androidx.room.Room
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
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private var Instance: LocalDatabase? = null

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): LocalDatabase {
        return Instance ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                LocalDatabase::class.java,
                "nutricheck_database"
            )
                .addMigrations(Migrations.MIGRATION_15_16, Migrations.MIGRATION_16_17)
                .build()
                .also { Instance = it }

        }

    }

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