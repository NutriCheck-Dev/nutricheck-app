package com.frontend.nutricheck.client.model.repositories.recipe

import android.content.Context
import com.frontend.nutricheck.client.model.data_layer.Recipe
import com.frontend.nutricheck.client.model.data_layer.RemoteApi
import com.frontend.nutricheck.client.model.logic.commands.CommandInvoker
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiRecipeRepository(private val context: Context) : BaseRecipeRepository {
    private val invoker = CommandInvoker()
    override val commandInvoker: CommandInvoker
        get() = invoker
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://dein-backend-url/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val remoteApi = retrofit.create(RemoteApi::class.java)

    override suspend fun getRecipe(recipeId: String): Recipe? {
        TODO("Not yet implemented")
    }

    override suspend fun saveRecipe(recipe: Recipe) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRecipe(recipeId: String) {
        TODO("Not yet implemented")
    }

}