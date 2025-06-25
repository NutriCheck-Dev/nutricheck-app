package com.frontend.nutricheck.client.model.mapper

import com.frontend.nutricheck.client.dto.RecipeDTO
import com.frontend.nutricheck.client.model.data_layer.Recipe

object RecipeMapper {
    fun toDto(recipe: Recipe): RecipeDTO = RecipeDTO(
        id = recipe.id,
        name = recipe.name,
        instructions = recipe.instructions,
        servings = recipe.servings,
        calories = recipe.calories,
        carbohydrates = recipe.carbohydrates,
        protein = recipe.protein,
        fat = recipe.fat,
        ingredients = recipe.ingredients.map { IngredientMapper.toDTO(it) }.toSet()
    )

    fun toEntity(recipeDto: RecipeDTO): Recipe = Recipe(
        id = recipeDto.id,
        name = recipeDto.name,
        instructions = recipeDto.instructions,
        servings = recipeDto.servings,
        calories = recipeDto.calories,
        carbohydrates = recipeDto.carbohydrates,
        protein = recipeDto.protein,
        fat = recipeDto.fat,
        ingredients = recipeDto.ingredients.map { IngredientMapper.toEntity(it) }.toSet()
    )
}