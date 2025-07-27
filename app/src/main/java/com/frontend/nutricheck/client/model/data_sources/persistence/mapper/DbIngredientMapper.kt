package com.frontend.nutricheck.client.model.data_sources.persistence.mapper

import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.IngredientEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.IngredientWithFoodProduct

object DbIngredientMapper {

    fun toIngredientEntity(ingredient: Ingredient) : IngredientEntity =
        IngredientEntity(
            id = ingredient.id,
            recipeId = ingredient.recipeId,
            foodProductId = ingredient.foodProduct.id,
            quantity = ingredient.quantity
        )

    fun toIngredient(ingredientWithFoodProduct: IngredientWithFoodProduct) : Ingredient {
        val ingredientEntity = ingredientWithFoodProduct.ingredientEntity
        val foodProduct = DbFoodProductMapper.toFoodProduct(ingredientWithFoodProduct.foodProductEntity)
        return Ingredient(
            id = ingredientEntity.id,
            recipeId = ingredientEntity.recipeId,
            foodProduct = foodProduct,
            quantity = ingredientEntity.quantity
        )
    }
}