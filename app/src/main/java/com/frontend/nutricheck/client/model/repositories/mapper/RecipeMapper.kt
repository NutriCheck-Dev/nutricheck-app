package com.frontend.nutricheck.client.model.repositories.mapper

import com.frontend.nutricheck.client.dto.RecipeDTO
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.flags.RecipeVisibility

/**
 * Mapper for converting [RecipeDTO] to [Recipe].
 */
object RecipeMapper {

    fun toDto(recipe: Recipe): RecipeDTO =
        RecipeDTO(
            id = recipe.id,
            name = recipe.name,
            instructions = recipe.instructions,
            servings = recipe.servings,
            calories = recipe.calories,
            carbohydrates = recipe.carbohydrates,
            protein = recipe.protein,
            fat = recipe.fat,
            ingredients = recipe.ingredients.map { IngredientMapper.toDTO(it) }
        )

    fun toData(recipeDto: RecipeDTO): Recipe =
        Recipe(
            id = recipeDto.id,
            name = recipeDto.name,
            calories = recipeDto.calories,
            carbohydrates = recipeDto.carbohydrates,
            protein = recipeDto.protein,
            fat = recipeDto.fat,
            ingredients = recipeDto.ingredients.map { IngredientMapper.toData(it) },
            instructions = recipeDto.instructions,
            servings = recipeDto.servings,
            visibility = RecipeVisibility.PUBLIC
        )
}