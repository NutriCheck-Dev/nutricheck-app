package com.frontend.nutricheck.client.model.data_sources.persistence.mapper

import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.IngredientEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.IngredientWithFoodProduct

object DbIngredientMapper {

    fun toIngredientEntity(ingredient: Ingredient) : IngredientEntity =
        IngredientEntity(
            recipeId = ingredient.recipeId,
            foodProductId = ingredient.foodProduct.id,
            quantity = ingredient.quantity
        )

      fun toIngredient(
        ingredientWithFoodProduct: IngredientWithFoodProduct
      ) : Ingredient = Ingredient(
            recipeId = ingredientWithFoodProduct.ingredient.recipeId,
            foodProduct = DbFoodProductMapper.toFoodProduct(ingredientWithFoodProduct.foodProduct),
            quantity =  ingredientWithFoodProduct.ingredient.quantity
        )
}