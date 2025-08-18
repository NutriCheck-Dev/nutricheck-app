package com.nutricheck.frontend.model.data_sources.persistence.mapper

import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.model.data_sources.data.flags.RecipeVisibility
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.*
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbMealMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.*
import org.junit.Test
import java.util.Date
import kotlin.test.assertEquals

class DbMealMapperTest {

    @Test
    fun `toMealEntity maps domain Meal to MealEntity`() {
        val meal = Meal(
            id = "meal123",
            calories = 500.0,
            carbohydrates = 50.0,
            protein = 20.0,
            fat = 10.0,
            date = Date(1700000000000),
            dayTime = DayTime.BREAKFAST,
            mealFoodItems = emptyList(),
            mealRecipeItems = emptyList()
        )

        val entity = DbMealMapper.toMealEntity(meal)

        assertEquals(meal.id, entity.id)
        assertEquals(meal.date, entity.historyDayDate)
        assertEquals(meal.dayTime, entity.dayTime)
        assertEquals(meal.calories, entity.calories)
        assertEquals(meal.carbohydrates, entity.carbohydrates)
        assertEquals(meal.protein, entity.protein)
        assertEquals(meal.fat, entity.fat)
    }

    @Test
    fun `toMeal maps MealWithAll to domain Meal`() {
        val mealEntity = MealEntity(
            id = "meal123",
            historyDayDate = Date(1700000000000),
            dayTime = DayTime.LUNCH,
            calories = 600.0,
            carbohydrates = 60.0,
            protein = 25.0,
            fat = 15.0
        )

        val foodProductEntity = FoodProductEntity(
            id = "food1",
            name = "Apple",
            calories = 52.0,
            carbohydrates = 14.0,
            protein = 0.3,
            fat = 0.2,
        )

        val mealFoodItemWithProduct = MealFoodItemWithProduct(
            mealFoodItem = MealFoodItemEntity(
                mealId = "meal123",
                foodProductId = "food1",
                quantity = 100.0,
                servings = 1,
                servingSize = ServingSize.ONEHOUNDREDGRAMS
            ),
            foodProduct = foodProductEntity
        )

        val recipeEntity = RecipeEntity(
            id = "recipe1",
            name = "Salad",
            calories = 100.0,
            carbohydrates = 10.0,
            protein = 2.0,
            fat = 1.0,
            servings = 2.0,
            instructions = "Mix it",
            visibility = RecipeVisibility.PUBLIC,
            deleted = false
        )

        val mealRecipeItemWithRecipe = MealRecipeItemWithRecipe(
            mealRecipeItem = MealRecipeItemEntity(
                mealId = "meal123",
                recipeId = "recipe1",
                quantity = 2.0
            ),
            recipeWithIngredients = RecipeWithIngredients(
                recipe = recipeEntity,
                ingredients = emptyList()
            )
        )

        val mealWithAll = MealWithAll(
            meal = mealEntity,
            mealFoodItems = listOf(mealFoodItemWithProduct),
            mealRecipeItems = listOf(mealRecipeItemWithRecipe)
        )

        val meal = DbMealMapper.toMeal(mealWithAll)

        assertEquals("meal123", meal.id)
        assertEquals(600.0, meal.calories)
        assertEquals(1, meal.mealFoodItems.size)
        assertEquals("Apple", meal.mealFoodItems.first().foodProduct.name)
        assertEquals(1, meal.mealRecipeItems.size)
        assertEquals("Salad", meal.mealRecipeItems.first().recipe.name)
    }
}
