package com.frontend.nutricheck.client.model.repositories

import com.frontend.nutricheck.client.model.repositories.appSetting.AppSettingRepository
import com.frontend.nutricheck.client.model.repositories.appSetting.AppSettingRepositoryImpl
import com.frontend.nutricheck.client.model.repositories.foodproducts.FoodProductRepository
import com.frontend.nutricheck.client.model.repositories.foodproducts.FoodProductRepositoryImpl
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepository
import com.frontend.nutricheck.client.model.repositories.history.HistoryRepositoryImpl
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepositoryImpl
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module for binding repository implementations to their interfaces.
 * This allows for dependency injection of repository instances throughout the application.
 */
@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindRecipeRepository(
        recipeRepositoryImpl: RecipeRepositoryImpl
    ): RecipeRepository

    @Singleton
    @Binds
    abstract fun bindFoodProductRepository(
        foodProductRepositoryImpl: FoodProductRepositoryImpl
    ): FoodProductRepository

    @Singleton
    @Binds
    abstract fun bindUserDataRepository(
        userDataRepositoryImpl: UserDataRepositoryImpl
    ): UserDataRepository

    @Singleton
    @Binds
    abstract fun bindHistoryRepository(
        historyRepositoryImpl: HistoryRepositoryImpl
    ): HistoryRepository

    @Singleton
    @Binds
    abstract fun bindAppSettingRepository(
        appSettingRepositoryImpl: AppSettingRepositoryImpl
    ): AppSettingRepository
}