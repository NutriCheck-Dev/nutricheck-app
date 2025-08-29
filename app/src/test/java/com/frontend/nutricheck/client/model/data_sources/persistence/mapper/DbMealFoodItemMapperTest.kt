package com.frontend.nutricheck.client.model.data_sources.persistence.mapper

import com.frontend.nutricheck.client.ui.view_model.TestDataFactory
import org.junit.Test
import org.junit.Assert.assertEquals

class DbMealFoodItemMapperTest {

    val mealFoodItem = TestDataFactory.createDefaultMealItem()
    val mealFoodItemEntity = TestDataFactory.createDefaultMealFoodItemEntity()
    @Test
    fun `toEntity should convert MealFoodItem to MealFoodItemEntity`() {
        val entity = DbMealFoodItemMapper.toMealFoodItemEntity(mealFoodItem)
        assertEquals(mealFoodItemEntity, entity)
    }
    @Test
    fun `toMealFoodIem should convert MealFoodItemWithProduct to MealFoodItem`() {
        val mealFoodItemWithProduct = TestDataFactory.createDefaultFoodItemsWithProduct()
        val mealFoodItemResult = DbMealFoodItemMapper.toMealFoodItem(mealFoodItemWithProduct)
        assertEquals(mealFoodItem, mealFoodItemResult)
    }

}