package com.nutricheck.frontend.model.data_sources.persistence.mapper

import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbFoodProductMapper
import org.junit.Assert.assertEquals
import org.junit.Test

class DbFoodProductMapperTest {

    @Test
    fun `toFoodProductEntity should map correctly`() {
        val domain = FoodProduct(
            id = "123",
            name = "Apfel",
            calories = 52.0,
            carbohydrates = 14.0,
            protein = 0.3,
            fat = 0.2
        )

        val entity = DbFoodProductMapper.toFoodProductEntity(domain)

        assertEquals(domain.id, entity.id)
        assertEquals(domain.name, entity.name)
        assertEquals(domain.calories, entity.calories, 0.001)
        assertEquals(domain.carbohydrates, entity.carbohydrates, 0.001)
        assertEquals(domain.protein, entity.protein, 0.001)
        assertEquals(domain.fat, entity.fat, 0.001)
    }

    @Test
    fun `toFoodProduct should map correctly`() {
        val entity = FoodProductEntity(
            id = "456",
            name = "Banane",
            calories = 89.0,
            carbohydrates = 23.0,
            protein = 1.1,
            fat = 0.3
        )

        val domain = DbFoodProductMapper.toFoodProduct(entity)

        assertEquals(entity.id, domain.id)
        assertEquals(entity.name, domain.name)
        assertEquals(entity.calories, domain.calories, 0.001)
        assertEquals(entity.carbohydrates, domain.carbohydrates, 0.001)
        assertEquals(entity.protein, domain.protein, 0.001)
        assertEquals(entity.fat, domain.fat, 0.001)
    }
}
