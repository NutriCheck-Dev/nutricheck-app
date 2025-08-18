package com.frontend.nutricheck.client.model.data_sources.persistence.mapper

import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.MealFoodItem
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealFoodItemEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealFoodItemWithProduct
import org.junit.Test
import kotlin.test.assertEquals

class DbMealFoodItemMapperTest {

    private val sampleFoodProduct = FoodProduct(
        id = "food123",
        name = "Banana",
        calories = 90.0,
        protein = 1.0,
        carbohydrates = 23.0,
        fat = 0.0,
        servings = 1,
        servingSize = ServingSize.ONEHOUNDREDGRAMS
    )

    private val sampleFoodProductEntity = FoodProductEntity(
        id = "food123",
        name = "Banana",
        calories = 90.0,
        protein = 1.0,
        carbohydrates = 23.0,
        fat = 0.0,
    )

    @Test
    fun `toMealFoodItemEntity maps correctly`() {
        val mealFoodItem = MealFoodItem(
            mealId = "meal1",
            foodProduct = sampleFoodProduct,
            quantity = 150.0,
            servings = 2,
            servingSize = ServingSize.ONEHOUNDREDGRAMS
        )

        val entity = DbMealFoodItemMapper.toMealFoodItemEntity(mealFoodItem)

        assertEquals(mealFoodItem.mealId, entity.mealId)
        assertEquals(mealFoodItem.foodProduct.id, entity.foodProductId)
        assertEquals(mealFoodItem.quantity, entity.quantity)
        assertEquals(mealFoodItem.servings, entity.servings)
        assertEquals(mealFoodItem.servingSize, entity.servingSize)
    }

    @Test
    fun `toMealFoodItem maps correctly`() {
        val entity = MealFoodItemEntity(
            mealId = "meal1",
            foodProductId = "food123",
            quantity = 200.0,
            servings = 3,
            servingSize = ServingSize.ONEHOUNDREDGRAMS
        )

        val withProduct = MealFoodItemWithProduct(
            mealFoodItem = entity,
            foodProduct = sampleFoodProductEntity
        )

        val model = DbMealFoodItemMapper.toMealFoodItem(withProduct)

        assertEquals(entity.mealId, model.mealId)
        assertEquals(sampleFoodProduct.id, model.foodProduct.id)
        assertEquals(entity.quantity, model.quantity)
        assertEquals(entity.servings, model.servings)
        assertEquals(entity.servingSize, model.servingSize)
    }
}
