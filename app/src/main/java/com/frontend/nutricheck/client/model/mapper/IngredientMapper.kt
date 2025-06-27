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
    fun toEntityList(ingredientDTOs: List<IngredientDTO>): List<Ingredient> =
        ingredientDTOs.map { toEntity(it) }
    fun toDTOList(ingredients: List<Ingredient>): List<IngredientDTO> {
        TODO("Implement the conversion from List<Ingredient> to List<IngredientDTO>")
    }
}