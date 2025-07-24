package com.frontend.nutricheck.client.model.repositories.mapper

import com.frontend.nutricheck.client.dto.IngredientDTO
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.IngredientWithFoodProduct

object IngredientMapper {
    fun toDTO(ingredientWithFP: IngredientWithFoodProduct) : IngredientDTO = IngredientDTO(
        recipeId = ingredientWithFP.ingredient.recipeId,
        foodProductId = ingredientWithFP.ingredient.foodProductId,
        foodProduct = FoodProductMapper.toDTO(ingredientWithFP.foodProduct),
        quantity = ingredientWithFP.ingredient.quantity
    )

    fun toEntities(ingredientDTO: IngredientDTO) : Pair<Ingredient, FoodProduct> {
        val ingredient = Ingredient(
            recipeId = ingredientDTO.recipeId,
            foodProductId = ingredientDTO.foodProductId,
            quantity = ingredientDTO.quantity
        )
        val foodProduct = FoodProductMapper.toEntity(ingredientDTO.foodProduct)
        return ingredient to foodProduct
    }
}