package com.frontend.nutricheck.client.model.services

import com.frontend.nutricheck.client.model.data_layer.Recipe

interface IRecipeService {
    suspend fun uploadRecipe(recipe: Recipe)
    suspend fun deleteRecipe(recipeId: com.frontend.nutricheck.client.model.data_layer.FoodComponentId)
}