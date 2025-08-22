package com.frontend.nutricheck.client.model.repositories.mapper

import com.frontend.nutricheck.client.dto.IngredientDTO
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize
import kotlin.math.absoluteValue

/**
 * Mapper for converting between [Ingredient] and [IngredientDTO].
 */
object IngredientMapper {
    fun toDTO(ingredient: Ingredient) : IngredientDTO =
        IngredientDTO(
            recipeId = ingredient.recipeId,
            foodProductId = ingredient.foodProduct.id,
            foodProduct = FoodProductMapper.toDTO(ingredient.foodProduct),
            quantity = ingredient.quantity
    )

    fun toData(ingredientDTO: IngredientDTO): Ingredient {
        val mappedServings = mapQuantityToServings(ingredientDTO.quantity)
        return Ingredient(
            recipeId = ingredientDTO.recipeId,
            foodProduct = FoodProductMapper.toData(ingredientDTO.foodProduct),
            quantity = ingredientDTO.quantity,
            servings = mappedServings.first,
            servingSize = mappedServings.second
        )
    }

    fun mapQuantityToServings(
        quantity: Double,
        servingSizes: List<ServingSize> = ServingSize.entries.toList(),
        maxServings: Int = 200
    ): Pair<Int, ServingSize> {
        val candidates = servingSizes
            .map { size -> size to size.getAmount() }
            .sortedByDescending { it.second }
        for ((servingSize, sizeInGrams) in candidates) {
            if ((quantity % sizeInGrams).absoluteValue < 1e-6) {
                val servings = (quantity / sizeInGrams).toInt()
                if (servings in 1..maxServings) {
                    return servings to servingSize
                }
            }
        }
        return quantity.toInt() to ServingSize.ONEGRAM
    }
}