package com.frontend.nutricheck.client.model.data_sources.persistence.mapper

import com.frontend.nutricheck.client.ui.view_model.TestDataFactory
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class DbMealRecipeItemMapperTest {

    val mealRecipeItem = TestDataFactory.createDefaultMealRecipeItem()
    val mealRecipeItemEntity = TestDataFactory.createDefaultMealRecipeItemEntity()

    @Test
    fun `toEntity should convert MealRecipeItem to MealRecipeItemEntity`() {
        val entity = DbMealRecipeItemMapper.toMealRecipeItemEntity(mealRecipeItem)
        assertEquals(mealRecipeItemEntity, entity)
    }

    @Test
    fun `toMealRecipeItem should convert MealRecipeItemWithRecipe to MealRecipeItem`() {
        val mealRecipeItemWithRecipe = TestDataFactory.createDefaultMealRecipeItemWithRecipe()
        val mealRecipeItemResult = DbMealRecipeItemMapper.toMealRecipeItem(mealRecipeItemWithRecipe)
        assertEquals(mealRecipeItem, mealRecipeItemResult)
    }

}