package com.frontend.nutricheck.client.model.repositories.mapper

import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize
import com.frontend.nutricheck.client.ui.view_model.TestDataFactory
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class MealItemMapperTest {

    @Test
    fun `toData should convert MealItemDTO to MealFoodItem with default values`() {
        val mealItemDTO = TestDataFactory.createDefaultMealItemDTO()
        val mealId = "meal123"

        mockkObject(FoodProductMapper)
        val foodProduct = TestDataFactory.createDefaultFoodProduct()
        val foodProductDTO = TestDataFactory.createDefaultFoodProductDTO()
        every { FoodProductMapper.toData(foodProductDTO) } returns foodProduct

        val mealFoodItem = MealItemMapper.toData(mealItemDTO, mealId)

        assertEquals(mealId, mealFoodItem.mealId)
        assertEquals(1.0, mealFoodItem.quantity)
        assertEquals(1.0, mealFoodItem.servings)
        assertEquals(ServingSize.ONEHOUNDREDGRAMS, mealFoodItem.servingSize)
        assertEquals(foodProduct.id, mealFoodItem.foodProduct.id)

        unmockkObject(FoodProductMapper)
    }
}

