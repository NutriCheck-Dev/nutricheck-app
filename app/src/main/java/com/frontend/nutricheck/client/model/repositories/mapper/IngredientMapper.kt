package com.frontend.nutricheck.client.model.repositories.mapper

import com.frontend.nutricheck.client.dto.IngredientDTO
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize
import java.math.BigDecimal
import java.math.RoundingMode

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
        minServings: Double = 1.0,
        maxServings: Double = 200.0,
        decimals: Int = 2
    ): Pair<Double, ServingSize> {
        val quantityBigDecimal = BigDecimal.valueOf(quantity)
        val minBigDecimal = BigDecimal.valueOf(minServings)
        val maxBigDecimal = BigDecimal.valueOf(maxServings)

        val candidates = servingSizes
            .map { size -> size to BigDecimal.valueOf(size.getAmount()) }
            .sortedByDescending { (_, gramsBigDecimal) -> gramsBigDecimal }

        val tolerance = BigDecimal.ONE.movePointLeft(decimals + 1)

        for ((size, sizeBigDecimal) in candidates) {
            if (sizeBigDecimal.signum() <= 0) continue

            var servings = quantityBigDecimal.divide(sizeBigDecimal, decimals, RoundingMode.HALF_UP)

            if (servings < minBigDecimal) servings = minBigDecimal
            if (servings > maxBigDecimal) servings = maxBigDecimal

            val recon = servings.multiply(sizeBigDecimal)
            val difference = recon.subtract(quantityBigDecimal).abs()

            if (difference <= tolerance) {
                return servings.toDouble() to size
            }
        }

        var fallbackServings = quantityBigDecimal.setScale(decimals, RoundingMode.HALF_UP)
        if (fallbackServings < minBigDecimal) fallbackServings = minBigDecimal
        if (fallbackServings > maxBigDecimal) fallbackServings = maxBigDecimal
        return fallbackServings.toDouble() to ServingSize.ONEGRAM
    }
}