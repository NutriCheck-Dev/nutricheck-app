package com.nutricheck.frontend.model.dtomapper

import com.frontend.nutricheck.client.dto.FoodProductDTO
import com.frontend.nutricheck.client.dto.MealItemDTO
import com.frontend.nutricheck.client.model.repositories.mapper.MealItemMapper
import org.junit.Assert.assertEquals
import org.junit.Test

class MealItemMapperTest {

    @Test
    fun `toData should map MealItemDTO to MealFoodItem with fixed quantity`() {
        val foodDTO = FoodProductDTO(
            id = "food01",
            name = "Joghurt",
            calories = 90.0,
            carbohydrates = 10.0,
            protein = 5.0,
            fat = 3.5
        )

        val dto = MealItemDTO(
            mealId = "meal42",
            foodProduct = foodDTO,
            foodProductId = "food01"
        )

        val result = MealItemMapper.toData(dto)

        assertEquals("meal42", result.mealId)
        assertEquals("food01", result.foodProduct.id)
        assertEquals(1.0, result.quantity, 0.001) // feste Menge
    }
}
