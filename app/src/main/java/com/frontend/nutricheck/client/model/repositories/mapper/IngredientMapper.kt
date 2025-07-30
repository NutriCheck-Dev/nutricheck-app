package com.frontend.nutricheck.client.model.repositories.mapper

import com.frontend.nutricheck.client.dto.IngredientDTO
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient

object IngredientMapper {
    fun toDTO(ingredients: Ingredient) : IngredientDTO =
        IngredientDTO(
            recipeId = ingredients.recipeId,
            foodProductId = ingredients.foodProduct.id,
            foodProduct = FoodProductMapper.toDTO(ingredients.foodProduct),
            quantity = ingredients.quantity
    )

    fun toData(ingredientDTO: IngredientDTO): Ingredient =
        Ingredient(
            recipeId = ingredientDTO.recipeId,
            foodProduct = FoodProductMapper.toData(ingredientDTO.foodProduct),
            quantity = ingredientDTO.quantity
        )
}