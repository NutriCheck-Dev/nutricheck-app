package com.nutricheck.frontend.util

import android.content.Context
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.Result
import dagger.hilt.EntryPoints
import kotlinx.coroutines.runBlocking
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class SeedRemoteRecipeRule(
    private val context: Context,
    private val buildRecipe: () -> Recipe
) : TestWatcher() {

    override fun starting(description: Description) {
        val entryPoint = EntryPoints.get(context, RecipeRepoEntryPoint::class.java)
        val recipeRepository = entryPoint.recipeRepository()

        runBlocking {
            val recipe = buildRecipe()
            when (recipeRepository.uploadRecipe(recipe)) {
                is Result.Success -> null
                is Result.Error -> null
            }
        }
    }

    override fun finished(description: Description) { }
}