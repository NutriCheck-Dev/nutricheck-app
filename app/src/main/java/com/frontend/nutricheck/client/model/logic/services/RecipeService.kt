package com.frontend.nutricheck.client.model.services

import com.frontend.nutricheck.client.model.data_layer.Recipe
import com.frontend.nutricheck.client.model.data_layer.FoodComponentId
import com.frontend.nutricheck.client.model.repositories.IRecipeRepository

class RecipeService(
    private val recipeRepository: IRecipeRepository
) : IRecipeService {
    override suspend fun uploadRecipe(recipe: Recipe) {
        // Hier ggf. Validierung, API-Aufruf etc.
        recipeRepository.addRecipe(recipe)
    }

    override suspend fun deleteRecipe(recipeId: FoodComponentId) {
        recipeRepository.removeRecipe(recipeId)
    }
}