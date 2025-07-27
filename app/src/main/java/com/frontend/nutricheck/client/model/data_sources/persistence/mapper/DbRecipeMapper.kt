package com.frontend.nutricheck.client.model.data_sources.persistence.mapper

import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.RecipeEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.RecipeWithIngredients

object DbRecipeMapper {

    fun toRecipeEntity(recipe: Recipe) : RecipeEntity =
        RecipeEntity(
            id = recipe.id,
            name = recipe.name,
            calories = recipe.calories,
            carbohydrates = recipe.carbohydrates,
            protein = recipe.protein,
            fat = recipe.fat,
            servings = recipe.servings.toDouble(),
            instructions = recipe.instructions,
            visibility = recipe.visibility
        )

    fun toRecipe(recipeWithIngredients: RecipeWithIngredients) : Recipe {
        val recipeEntity = recipeWithIngredients.recipeEntity
        val ingredientEntities = recipeWithIngredients.ingredients
        val ingredients = ingredientEntities.map { ingredientWithFoodProduct ->
            DbIngredientMapper.toIngredient(ingredientWithFoodProduct)
        }
        return Recipe(
            id = recipeEntity.id,
            name = recipeEntity.name,
            calories = recipeEntity.calories,
            carbohydrates = recipeEntity.carbohydrates,
            protein = recipeEntity.protein,
            fat = recipeEntity.fat,
            ingredients = ingredients,
            servings = recipeEntity.servings.toInt(),
            instructions = recipeEntity.instructions,
            visibility = recipeEntity.visibility,
        )
    }
}