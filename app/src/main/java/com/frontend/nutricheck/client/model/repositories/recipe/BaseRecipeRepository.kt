package com.frontend.nutricheck.client.model.repositories.recipe

import com.frontend.nutricheck.client.model.data_layer.FoodComponentId
import com.frontend.nutricheck.client.model.data_layer.Recipe
import kotlinx.coroutines.flow.Flow

interface BaseRecipeRepository {
    suspend fun getRecipe(recipeId: FoodComponentId): Recipe?
    suspend fun saveRecipe(recipe: Recipe)
    suspend fun deleteRecipe(recipeId: FoodComponentId)
    suspend fun observeLocalRecipes(): Flow<Set<FoodComponentId>>
}