package com.frontend.nutricheck.client.model.data_sources.persistence

import android.content.Context
import androidx.room.Room
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.FoodDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.HistoryDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.RecipeDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.UserDataDao
import com.frontend.nutricheck.client.model.data_sources.persistence.dao.WeightDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): LocalDatabase {
        return Room.databaseBuilder(
            context,
            LocalDatabase::class.java,
            "nutricheck_database"
        ).fallbackToDestructiveMigration(false).build()
    }

    @Singleton
    @Provides
    fun provideFoodDao(database: LocalDatabase): FoodDao = database.foodDao()

    @Singleton
    @Provides
    fun provideRecipeDao(database: LocalDatabase): RecipeDao = database.recipeDao()

    @Singleton
    @Provides
    fun provideHistoryDao(database: LocalDatabase): HistoryDao = database.historyDao()

    @Singleton
    @Provides
    fun provideWeightDao(database: LocalDatabase): WeightDao = database.weightDao()

    @Singleton
    @Provides
    fun provideUserDataDao(database: LocalDatabase): UserDataDao = database.userDataDao()
}