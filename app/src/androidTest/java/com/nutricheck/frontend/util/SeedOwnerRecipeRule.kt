package com.nutricheck.frontend.util

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.flags.RecipeVisibility
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.runBlocking
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class SeedOwnerRecipeRule(
    private val application: Application = ApplicationProvider.getApplicationContext(),
    private val buildRecipe: () -> Recipe
) : TestRule {

    lateinit var seeded: Recipe
        private set

    override fun apply(base: Statement, description: Description) = object : Statement() {
        override fun evaluate() {
            val entry = EntryPointAccessors.fromApplication(application, RecipeRepoEntryPoint::class.java)
            val repo = entry.recipeRepository()

            runBlocking {
                val recipe = buildRecipe()
                val ownerRecipe = recipe.copy(visibility = RecipeVisibility.OWNER)
                repo.insertRecipe(ownerRecipe)
                seeded = ownerRecipe
            }

            base.evaluate()
        }
    }
}