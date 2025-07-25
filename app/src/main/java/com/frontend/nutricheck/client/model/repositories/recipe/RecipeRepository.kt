package com.frontend.nutricheck.client.model.repositories.recipe

import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.IngredientWithFoodProduct
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.RecipeWithIngredients
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    suspend fun searchRecipe(recipeName: String): Result<List<Recipe>>
    suspend fun insertRecipe(recipe: Recipe)
    suspend fun deleteRecipe(recipe: Recipe)
    fun getMyRecipesWithIngredients(): Flow<List<RecipeWithIngredients>>
    fun getRecipesWithIngredientsById(recipeId: String): Flow<RecipeWithIngredients>
    fun getOnlineRecipes(): Flow<List<Recipe>>
    fun getMyRecipes(): Flow<List<Recipe>>
    fun getRecipeById(recipeId: String): Flow<Recipe>
    suspend fun updateRecipe(recipe: Recipe)
    fun getIngredientsForRecipe(recipeId: String): Flow<List<IngredientWithFoodProduct>>
    suspend fun addIngredient(ingredient: Ingredient)
    suspend fun updateIngredient(ingredient: Ingredient)
    suspend fun removeIngredient(ingredient: Ingredient)
}