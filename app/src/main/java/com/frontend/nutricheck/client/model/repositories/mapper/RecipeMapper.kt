package com.frontend.nutricheck.client.model.repositories.mapper

import com.frontend.nutricheck.client.dto.RecipeDTO
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.IngredientWithFoodProduct

object RecipeMapper {

    fun toDto(recipe: Recipe, ingredientsWithProducts: List<IngredientWithFoodProduct>): RecipeDTO =
        RecipeDTO(
            id = recipe.id,
            name = recipe.name,
            instructions = recipe.instructions,
            servings = recipe.servings,
            calories = recipe.calories,
            carbohydrates = recipe.carbohydrates,
            protein = recipe.protein,
            fat = recipe.fat,
            ingredients = ingredientsWithProducts.map { IngredientMapper.toDTO(it) }.toSet()
        )

    fun toEntity(recipeDto: RecipeDTO): Recipe =
        Recipe(
            id = recipeDto.id,  // Falls id nullable im DTO ist
            name = recipeDto.name, // Optional Default
            instructions = recipeDto.instructions,
            servings = recipeDto.servings,
            calories = recipeDto.calories,
            carbohydrates = recipeDto.carbohydrates,
            protein = recipeDto.protein,
            fat = recipeDto.fat,
        )
    fun extractIngredientEntities(recipeDto: RecipeDTO): List<Ingredient> =
        recipeDto.ingredients.map { IngredientMapper.toEntities(it).first }

    fun extractFoodProductEntities(recipeDto: RecipeDTO): List<FoodProduct> =
        recipeDto.ingredients.mapNotNull { it.foodProduct?.let(FoodProductMapper::toEntity) }

}