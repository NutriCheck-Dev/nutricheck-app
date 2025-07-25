package com.frontend.nutricheck.client.model.data_sources.remote

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private const val BASE_URL = "https://1d25b66e-2926-4996-bb00-2d7fe74c098f.ka.bw-cloud-instance.org/"

    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Oder: Kein Logging (für Produktion)
        /*
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.NONE
        }
        */

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            // Hier könnten später Authentifizierungs-Interceptor etc. hinzukommen
            .build()
    }

    private val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Den benutzerdefinierten OkHttpClient verwenden
            .addConverterFactory(
                GsonConverterFactory.create(GsonBuilder().create())
            ) // Gson-Instanz ggf. anpassen
            .build()
    }

    fun getInstance(): Retrofit = retrofitInstance
}