package com.nutricheck.frontend.model.dtomapper

import com.frontend.nutricheck.client.dto.FoodProductDTO
import com.frontend.nutricheck.client.dto.IngredientDTO
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.repositories.mapper.IngredientMapper
import org.junit.Assert.assertEquals
import org.junit.Test

class IngredientMapperTest {

    @Test
    fun `toDTO should map Ingredient to IngredientDTO correctly`() {
        val food = FoodProduct(
            id = "f001",
            name = "Reis",
            calories = 130.0,
            carbohydrates = 28.0,
            protein = 2.7,
            fat = 0.3
        )

        val ingredient = Ingredient(
            recipeId = "r001",
            foodProduct = food,
            quantity = 2.5
        )

        val dto = IngredientMapper.toDTO(ingredient)

        assertEquals("r001", dto.recipeId)
        assertEquals("f001", dto.foodProductId)
        assertEquals(2.5, dto.quantity, 0.001)
        assertEquals("Reis", dto.foodProduct.name)
        assertEquals(130.0, dto.foodProduct.calories, 0.001)
    }

    @Test
    fun `toData should map IngredientDTO to Ingredient correctly`() {
        val foodDTO = FoodProductDTO(
            id = "f002",
            name = "Karotte",
            calories = 41.0,
            carbohydrates = 10.0,
            protein = 0.9,
            fat = 0.2
        )

        val dto = IngredientDTO(
            recipeId = "r002",
            foodProductId = "f002",
            foodProduct = foodDTO,
            quantity = 1.0
        )

        val ingredient = IngredientMapper.toData(dto)

        assertEquals("r002", ingredient.recipeId)
        assertEquals("f002", ingredient.foodProduct.id)
        assertEquals("Karotte", ingredient.foodProduct.name)
        assertEquals(1.0, ingredient.quantity, 0.001)
    }
}
