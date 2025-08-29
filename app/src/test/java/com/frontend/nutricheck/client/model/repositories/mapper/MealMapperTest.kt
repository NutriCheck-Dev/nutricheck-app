package com.frontend.nutricheck.client.model.repositories.mapper

import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.ui.view_model.TestDataFactory
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.Test
import org.junit.Assert.assertEquals
import java.util.Date

class MealMapperTest {



    @Test
    fun `toDTO should convert Meal to MealDTO`() {
        val mealDTO = TestDataFactory.createDefaultMealDTO()
        val mealItemDto = TestDataFactory.createDefaultMealItemDTO()
        val mealFoodItem = TestDataFactory.createDefaultMealItem()
        val id = "testMealId"

        mockkObject(MealItemMapper)
        every { MealItemMapper.toData(mealItemDto, id) } returns mealFoodItem

        val meal = MealMapper.toData(mealDTO)

        assertEquals(mealDTO.calories, meal.calories, 0.0)
        assertEquals(mealDTO.carbohydrates, meal.carbohydrates, 0.0)
        assertEquals(mealDTO.protein, meal.protein, 0.0)
        assertEquals(mealDTO.fat, meal.fat, 0.0)
        assertEquals(Date().toString(), meal.date.toString())
        assertEquals(DayTime.dateToDayTime(Date()), meal.dayTime)
        assertEquals(mealDTO.items.size, meal.mealFoodItems.size)
        unmockkObject(MealItemMapper)
    }
}