package com.frontend.nutricheck.client.model.repositories.mapper

import com.frontend.nutricheck.client.dto.MealDTO
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import java.util.Date
import java.util.UUID

/**
 * Mapper for converting [MealDTO] to [Meal].
 */
object MealMapper {
    fun toData(mealDTO: MealDTO): Meal {
        val id = UUID.randomUUID().toString()
        return Meal(
            id = id,
            calories = mealDTO.calories,
            carbohydrates = mealDTO.carbohydrates,
            protein = mealDTO.protein,
            fat = mealDTO.fat,
            date = Date(),
            dayTime = DayTime.dateToDayTime(Date()),
            mealFoodItems = mealDTO.items.map { MealItemMapper.toData(it, id) },
            mealRecipeItems = listOf()
        )
    }

}