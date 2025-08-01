package com.frontend.nutricheck.client.model.data_sources.persistence.mapper

import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealWithAll

object DbMealMapper {

    fun toMealEntity(meal: Meal) : MealEntity =
        MealEntity(
            id = meal.id,
            dayTime = meal.dayTime,
            calories = meal.calories,
            carbohydrates = meal.carbohydrates,
            protein = meal.protein,
            fat = meal.fat
        )

    fun toMeal(mealWithContents: MealWithAll) : Meal =
        Meal(
            id = mealWithContents.meal.id,
            calories = mealWithContents.meal.calories,
            carbohydrates = mealWithContents.meal.carbohydrates,
            protein = mealWithContents.meal.protein,
            fat = mealWithContents.meal.fat,
            date = mealWithContents.meal.historyDayDate,
            dayTime = mealWithContents.meal.dayTime,
            mealFoodItems = mealWithContents.mealFoodItems.map
            { DbMealFoodItemMapper.toMealFoodItem(it) },
            mealRecipeItems = mealWithContents.mealRecipeItems.map
            { DbMealRecipeItemMapper.toMealRecipeItem(it) },
        )
}