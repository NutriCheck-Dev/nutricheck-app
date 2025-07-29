package com.frontend.nutricheck.client.model.repositories.mapper

import com.frontend.nutricheck.client.dto.MealDTO
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import java.util.UUID

object MealMapper {
    fun toData(mealDTO: MealDTO): Meal =
        Meal(
            id = UUID.randomUUID().toString(),
            calories = mealDTO.calories,
            carbohydrates = mealDTO.carbohydrates,
            protein = mealDTO.protein,
            fat = mealDTO.fat,
            date = TODO(),
            dayTime = TODO(),
            mealFoodItems = TODO(),
            mealRecipeItem = TODO()
        )
}