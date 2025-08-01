package com.nutricheck.frontend.model.dtomapper

import com.frontend.nutricheck.client.dto.FoodProductDTO
import com.frontend.nutricheck.client.dto.MealDTO
import com.frontend.nutricheck.client.dto.MealItemDTO
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.model.repositories.mapper.MealMapper
import org.junit.Assert.*
import org.junit.Test

class MealMapperTest {

    @Test
    fun `toData should map MealDTO to Meal with generated id and defaults`() {
        val foodDTO = FoodProductDTO(
            id = "food01",
            name = "Apfel",
            calories = 52.0,
            carbohydrates = 14.0,
            protein = 0.3,
            fat = 0.2
        )

        val itemDTO = MealItemDTO(
            mealId = "anyMealId",  // wird überschrieben intern
            foodProduct = foodDTO,
            foodProductId = "food01",
        )

        val mealDTO = MealDTO(
            calories = 200.0,
            carbohydrates = 30.0,
            protein = 10.0,
            fat = 5.0,
            items = setOf(itemDTO)
        )

        val result: Meal = MealMapper.toData(mealDTO)

        // 🔍 Prüfung
        assertNotNull(result.id) // wird generiert
        assertTrue(result.id.isNotBlank())
        assertEquals(200.0, result.calories, 0.001)
        assertEquals(DayTime.BREAKFAST, result.dayTime)
        assertNotNull(result.date)
        assertEquals(1, result.mealFoodItems.size)
        assertEquals("food01", result.mealFoodItems.first().foodProduct.id)
        assertTrue(result.mealRecipeItem.isEmpty())
    }
}
