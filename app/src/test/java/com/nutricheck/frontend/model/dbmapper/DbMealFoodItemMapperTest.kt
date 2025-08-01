package com.nutricheck.frontend.model.dbmapper

import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealFoodItemEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealFoodItemWithProduct
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbMealFoodItemMapper
import org.junit.Assert.assertEquals
import org.junit.Test

class DbMealFoodItemMapperTest {

    @Test
    fun `toMealFoodItemEntity should map correctly`() {
        val foodProduct = FoodProduct(
            id = "food123",
            name = "Haferflocken",
            calories = 370.0,
            carbohydrates = 60.0,
            protein = 13.0,
            fat = 7.0
        )

        val domain = MealFoodItem(
            mealId = "meal001",
            foodProduct = foodProduct,
            quantity = 1.5
        )

        val entity = DbMealFoodItemMapper.toMealFoodItemEntity(domain)

        assertEquals(domain.mealId, entity.mealId)
        assertEquals(domain.foodProduct.id, entity.foodProductId)
        assertEquals(domain.quantity, entity.quantity, 0.001)
    }

    @Test
    fun `toMealFoodItem should map correctly`() {
        val foodEntity = FoodProductEntity(
            id = "food456",
            name = "Banane",
            calories = 89.0,
            carbohydrates = 23.0,
            protein = 1.1,
            fat = 0.3
        )

        val itemEntity = MealFoodItemEntity(
            mealId = "meal002",
            foodProductId = "food456",
            quantity = 2.0
        )

        val relation = MealFoodItemWithProduct(
            mealFoodItem = itemEntity,
            foodProduct = foodEntity
        )

        val domain = DbMealFoodItemMapper.toMealFoodItem(relation)

        assertEquals(itemEntity.mealId, domain.mealId)
        assertEquals(itemEntity.quantity, domain.quantity, 0.001)
        assertEquals(foodEntity.id, domain.foodProduct.id)
        assertEquals(foodEntity.name, domain.foodProduct.name)
    }
}
