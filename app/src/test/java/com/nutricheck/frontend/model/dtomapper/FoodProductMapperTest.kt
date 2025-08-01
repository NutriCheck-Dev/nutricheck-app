package com.nutricheck.frontend.model.dtomapper

import com.frontend.nutricheck.client.dto.FoodProductDTO
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.repositories.mapper.FoodProductMapper
import org.junit.Assert.assertEquals
import org.junit.Test

class FoodProductMapperTest {

    @Test
    fun `toDTO should map FoodProduct to FoodProductDTO correctly`() {
        val product = FoodProduct(
            id = "p001",
            name = "Mandeln",
            calories = 600.0,
            carbohydrates = 20.0,
            protein = 21.0,
            fat = 52.0
        )

        val dto = FoodProductMapper.toDTO(product)

        assertEquals(product.id, dto.id)
        assertEquals(product.name, dto.name)
        assertEquals(product.calories, dto.calories, 0.001)
        assertEquals(product.carbohydrates, dto.carbohydrates, 0.001)
        assertEquals(product.protein, dto.protein, 0.001)
        assertEquals(product.fat, dto.fat, 0.001)
    }

    @Test
    fun `toData should map FoodProductDTO to FoodProduct correctly`() {
        val dto = FoodProductDTO(
            id = "p002",
            name = "Joghurt",
            calories = 90.0,
            carbohydrates = 10.0,
            protein = 5.0,
            fat = 3.5
        )

        val product = FoodProductMapper.toData(dto)

        assertEquals(dto.id, product.id)
        assertEquals(dto.name, product.name)
        assertEquals(dto.calories, product.calories, 0.001)
        assertEquals(dto.carbohydrates, product.carbohydrates, 0.001)
        assertEquals(dto.protein, product.protein, 0.001)
        assertEquals(dto.fat, product.fat, 0.001)
    }
}
