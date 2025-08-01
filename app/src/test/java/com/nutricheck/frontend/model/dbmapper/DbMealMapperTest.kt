package com.nutricheck.frontend.model.dbmapper

import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.*
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbMealMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.*
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class DbMealMapperTest {


    @Test
    fun `toMealEntity should map Meal to MealEntity correctly`() {
        val meal = Meal(
            id = "meal001",
            calories = 500.0,
            carbohydrates = 50.0,
            protein = 30.0,
            fat = 20.0,
            date = Date(),
            dayTime = DayTime.LUNCH,
            mealFoodItems = emptyList(),
            mealRecipeItem = emptyList()
        )

        val entity = DbMealMapper.toMealEntity(meal)

        assertEquals(meal.id, entity.id)
        assertEquals(meal.dayTime, entity.dayTime)
        assertEquals(meal.calories, entity.calories, 0.001)
        assertEquals(meal.carbohydrates, entity.carbohydrates, 0.001)
        assertEquals(meal.protein, entity.protein, 0.001)
        assertEquals(meal.fat, entity.fat, 0.001)
    }

    @Test
    fun `toMeal should map full MealWithAll relation correctly`() {
        val testDate = Date()

        val foodEntity = FoodProductEntity(
            id = "food1",
            name = "Brokkoli",
            calories = 30.0,
            carbohydrates = 5.0,
            protein = 3.0,
            fat = 0.3
        )

        val ingredientEntity = IngredientEntity(
            recipeId = "recipe1",
            foodProductId = "food1",
            quantity = 1.0
        )

        val recipeEntity = RecipeEntity(
            id = "recipe1",
            name = "Gemüsepfanne",
            calories = 200.0,
            carbohydrates = 20.0,
            protein = 10.0,
            fat = 5.0
        )

        val mealEntity = MealEntity(
            id = "meal1",
            dayTime = DayTime.DINNER,
            calories = 500.0,
            carbohydrates = 60.0,
            protein = 25.0,
            fat = 20.0,
            historyDayDate = testDate
        )

        val mealFoodItem = MealFoodItemEntity(
            mealId = "meal1",
            foodProductId = "food1",
            quantity = 1.5
        )

        val mealRecipeItem = MealRecipeItemEntity(
            mealId = "meal1",
            recipeId = "recipe1",
            quantity = 2.0
        )

        val ingredientWithFood = IngredientWithFoodProduct(
            ingredient = ingredientEntity,
            foodProduct = foodEntity
        )

        val recipeWithIngredients = RecipeWithIngredients(
            recipe = recipeEntity,
            ingredients = listOf(ingredientWithFood)
        )

        val mealFoodItemWithProduct = MealFoodItemWithProduct(
            mealFoodItem = mealFoodItem,
            foodProduct = foodEntity
        )

        val mealRecipeItemWithRecipe = MealRecipeItemWithRecipe(
            mealRecipeItem = mealRecipeItem,
            recipeWithIngredients = recipeWithIngredients
        )

        val mealWithAll = MealWithAll(
            meal = mealEntity,
            mealFoodItems = listOf(mealFoodItemWithProduct),
            mealRecipeItems = listOf(mealRecipeItemWithRecipe)
        )

        val result = DbMealMapper.toMeal(mealWithAll)

        // ✅ Assertions
        assertEquals("meal1", result.id)
        assertEquals(DayTime.DINNER, result.dayTime)
        assertEquals(500.0, result.calories, 0.001)
        assertEquals(testDate, result.date)

        // MealFoodItem
        assertEquals(1, result.mealFoodItems.size)
        assertEquals("food1", result.mealFoodItems.first().foodProduct.id)
        assertEquals(1.5, result.mealFoodItems.first().quantity, 0.001)

        // MealRecipeItem
        assertEquals(1, result.mealRecipeItem.size)
        assertEquals("recipe1", result.mealRecipeItem.first().recipe.id)
        assertEquals(2.0, result.mealRecipeItem.first().quantity, 0.001)

        // Ingredient inside Recipe
        val firstIngredient = result.mealRecipeItem.first().recipe.ingredients.first()
        assertEquals("food1", firstIngredient.foodProduct.id)
        assertEquals(1.0, firstIngredient.quantity, 0.001)
    }
}
