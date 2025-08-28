package com.nutricheck.frontend.util

import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface RecipeRepoEntryPoint {
    fun recipeRepository(): RecipeRepository
}