package com.frontend.nutricheck.client.model.data_sources.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "1d25b66e-2926-4996-bb00-2d7fe74c098f.ka.bw-cloud-instance.org"

    fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}