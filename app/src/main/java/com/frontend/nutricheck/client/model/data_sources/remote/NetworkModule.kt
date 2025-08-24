package com.frontend.nutricheck.client.model.data_sources.remote

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Dagger Hilt Module for providing network-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return RetrofitInstance.getInstance()
    }
    @Singleton
    @Provides
    fun provideRemoteApi(retrofit : Retrofit): RemoteApi {
        return retrofit.create(RemoteApi::class.java)
    }
}