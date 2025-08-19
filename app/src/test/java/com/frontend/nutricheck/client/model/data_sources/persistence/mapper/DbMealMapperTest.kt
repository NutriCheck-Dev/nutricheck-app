package com.frontend.nutricheck.client.model.data_sources.persistence.mapper

import com.frontend.nutricheck.client.ui.view_model.TestDataFactory
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class DbMealMapperTest {

    val meal = TestDataFactory.createDefaultMeal()
    val mealEntity = TestDataFactory.createDefaultMealEntity()

    @Test
    fun `toEntity should convert Meal to MealEntity`() {
        val entity = DbMealMapper.toMealEntity(meal)
        assertEquals(mealEntity.id, entity.id)
        assertEquals(mealEntity.dayTime, entity.dayTime)
        assertEquals(mealEntity.calories, entity.calories)
        assertEquals(mealEntity.carbohydrates, entity.carbohydrates)
        assertEquals(mealEntity.protein, entity.protein)
        assertEquals(mealEntity.fat, entity.fat)
        assertEquals(mealEntity.historyDayDate.toString(), entity.historyDayDate.toString())
    }

    @Test
    fun `toMeal should convert MealWithAll to Meal`() {
        val mealWithContents = TestDataFactory.createDefaultMealWithAll()
        val mealResult = DbMealMapper.toMeal(mealWithContents)
        assertEquals(meal.id, mealResult.id)
        assertEquals(meal.calories, mealResult.calories)
        assertEquals(meal.carbohydrates, mealResult.carbohydrates)
        assertEquals(meal.protein, mealResult.protein)
        assertEquals(meal.fat, mealResult.fat)
        assertEquals(meal.date.toString(), mealResult.date.toString())
        assertEquals(meal.dayTime, mealResult.dayTime)
        assertEquals(meal.mealFoodItems.size, mealResult.mealFoodItems.size)
        assertEquals(meal.mealRecipeItems.size, mealResult.mealRecipeItems.size)
    }

}