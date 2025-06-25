package com.frontend.nutricheck.client.model.mapper

import com.frontend.nutricheck.client.dto.IngredientDTO
import com.frontend.nutricheck.client.model.data_layer.Ingredient

object IngredientMapper {
    fun toDTO(ingredient: Ingredient) : IngredientDTO = IngredientDTO(
        recipeId = ingredient.recipeId,
        foodProductId = ingredient.foodProductId,
        foodProduct = FoodProductMapper.toDTO(ingredient.foodProduct),
        quantity = ingredient.quantity
    )

    fun toEntity(ingredientDTO: IngredientDTO) : Ingredient = Ingredient(
        recipeId = ingredientDTO.recipeId,
        foodProductId = ingredientDTO.foodProductId,
        foodProduct = FoodProductMapper.toEntity(ingredientDTO.foodProduct),
        quantity = ingredientDTO.quantity
    )
}