package com.frontend.nutricheck.client.model.data_sources.persistence.mapper

import com.frontend.nutricheck.client.model.data_sources.data.MealRecipeItem
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.flags.RecipeVisibility
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealRecipeItemEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.RecipeEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.MealRecipeItemWithRecipe
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.RecipeWithIngredients
import org.junit.Test
import kotlin.test.assertEquals

class DbMealRecipeItemMapperTest {

    private val sampleRecipe = Recipe(
        id = "recipe123",
        name = "Test Recipe",
        calories = 500.0,
        carbohydrates = 50.0,
        protein = 25.0,
        fat = 20.0,
        servings = 2,
        instructions = "Mix everything",
        visibility = RecipeVisibility.PUBLIC,
        ingredients = emptyList()
    )

    private val sampleMealRecipeItem = MealRecipeItem(
        mealId = "meal123",
        recipe = sampleRecipe,
        quantity = 2.0,
        servings = sampleRecipe.servings
    )

    @Test
    fun `toMealRecipeItemEntity maps correctly`() {
        val entity = DbMealRecipeItemMapper.toMealRecipeItemEntity(sampleMealRecipeItem)

        assertEquals(sampleMealRecipeItem.mealId, entity.mealId)
        assertEquals(sampleMealRecipeItem.recipe.id, entity.recipeId)
        assertEquals(sampleMealRecipeItem.quantity, entity.quantity)
    }

    @Test
    fun `toMealRecipeItem maps correctly`() {
        val recipeEntity = RecipeEntity(
            id = sampleRecipe.id,
            name = sampleRecipe.name,
            calories = sampleRecipe.calories,
            carbohydrates = sampleRecipe.carbohydrates,
            protein = sampleRecipe.protein,
            fat = sampleRecipe.fat,
            servings = sampleRecipe.servings.toDouble(),
            instructions = sampleRecipe.instructions,
            visibility = sampleRecipe.visibility,
            deleted = false
        )

        val relation = MealRecipeItemWithRecipe(
            mealRecipeItem = MealRecipeItemEntity(
                mealId = "meal123",
                recipeId = "recipe123",
                quantity = 2.0
            ),
            recipeWithIngredients = RecipeWithIngredients(
                recipe = recipeEntity,
                ingredients = emptyList()
            )
        )

        val domain = DbMealRecipeItemMapper.toMealRecipeItem(relation)

        assertEquals(relation.mealRecipeItem.mealId, domain.mealId)
        assertEquals(relation.mealRecipeItem.recipeId, domain.recipe.id)
        assertEquals(relation.mealRecipeItem.quantity, domain.quantity)
    }
}
