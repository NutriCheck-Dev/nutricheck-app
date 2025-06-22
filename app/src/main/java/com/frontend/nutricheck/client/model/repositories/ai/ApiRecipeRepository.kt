package com.frontend.nutricheck.client.model.repositories.ai

import com.frontend.nutricheck.client.model.data_layer.FoodComponentId
import com.frontend.nutricheck.client.model.data_layer.Recipe
import com.frontend.nutricheck.client.model.repositories.recipe.BaseRecipeRepository
import kotlinx.coroutines.flow.Flow

class ApiRecipeRepository: BaseRecipeRepository {
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