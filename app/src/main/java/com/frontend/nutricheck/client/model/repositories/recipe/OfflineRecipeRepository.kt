package com.frontend.nutricheck.client.model.repositories.recipe

import com.frontend.nutricheck.client.model.data_layer.FoodComponentId
import com.frontend.nutricheck.client.model.data_layer.Recipe
import kotlinx.coroutines.flow.Flow

class OfflineRecipeRepository: BaseRecipeRepository {
    override suspend fun getRecipe(recipeId: FoodComponentId): Recipe? {
        TODO("Not yet implemented")
    }

    override suspend fun saveRecipe(recipe: Recipe) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRecipe(recipeId: FoodComponentId) {
        TODO("Not yet implemented")
    }

    override suspend fun observeLocalRecipes(): Flow<Set<FoodComponentId>> {
        TODO("Not yet implemented")
    }
}