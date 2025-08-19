package com.frontend.nutricheck.client.model.repositories.mapper

import com.nutricheck.frontend.TestDataFactory
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class FoodProductMapperTest {

    val foodProduct = TestDataFactory.createDefaultFoodProduct()
    val foodProductDTO = TestDataFactory.createDefaultFoodProductDTO()

    @Test
    fun `toDTO should convert FoodProduct to FoodProductDTO`() {
        val dto = FoodProductMapper.toDTO(foodProduct)
        assertEquals(foodProduct.id, dto.id)
        assertEquals(foodProduct.name, dto.name)
        assertEquals(foodProduct.calories, dto.calories)
        assertEquals(foodProduct.carbohydrates, dto.carbohydrates)
        assertEquals(foodProduct.protein, dto.protein)
        assertEquals(foodProduct.fat, dto.fat)
    }

    @Test
    fun `toData should convert FoodProductDTO to FoodProduct`() {
        val data = FoodProductMapper.toData(foodProductDTO)
        assertEquals(foodProductDTO.id, data.id)
        assertEquals(foodProductDTO.name, data.name)
        assertEquals(foodProductDTO.calories, data.calories, 0.0)
        assertEquals(foodProductDTO.carbohydrates, data.carbohydrates, 0.0)
        assertEquals(foodProductDTO.protein, data.protein, 0.0)
        assertEquals(foodProductDTO.fat, data.fat, 0.0)
    }

}