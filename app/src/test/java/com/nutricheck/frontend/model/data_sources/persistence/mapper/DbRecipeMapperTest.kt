package com.nutricheck.frontend.model.data_sources.persistence.mapper

import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.flags.RecipeVisibility
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.RecipeEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbRecipeMapper
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.RecipeWithIngredients
import org.junit.Test
import kotlin.test.assertEquals

class DbRecipeMapperTest {

    private val sampleRecipe = Recipe(
        id = "recipe123",
        name = "Test Recipe",
        calories = 500.0,
        carbohydrates = 50.0,
        protein = 25.0,
        fat = 20.0,
        servings = 2,
        instructions = "Mix ingredients",
        visibility = RecipeVisibility.PUBLIC,
        ingredients = emptyList()
    )

    @Test
    fun `toRecipeEntity maps correctly`() {
        val entity = DbRecipeMapper.toRecipeEntity(sampleRecipe, delete = true)

        assertEquals(sampleRecipe.id, entity.id)
        assertEquals(sampleRecipe.name, entity.name)
        assertEquals(sampleRecipe.calories, entity.calories)
        assertEquals(sampleRecipe.carbohydrates, entity.carbohydrates)
        assertEquals(sampleRecipe.protein, entity.protein)
        assertEquals(sampleRecipe.fat, entity.fat)
        assertEquals(sampleRecipe.servings.toDouble(), entity.servings)
        assertEquals(sampleRecipe.instructions, entity.instructions)
        assertEquals(sampleRecipe.visibility, entity.visibility)
        assertEquals(true, entity.deleted)
    }

    @Test
    fun `toRecipe maps correctly`() {
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

        val relation = RecipeWithIngredients(
            recipe = recipeEntity,
            ingredients = emptyList()
        )

        val mapped = DbRecipeMapper.toRecipe(relation)

        assertEquals(recipeEntity.id, mapped.id)
        assertEquals(recipeEntity.name, mapped.name)
        assertEquals(recipeEntity.calories, mapped.calories)
        assertEquals(recipeEntity.carbohydrates, mapped.carbohydrates)
        assertEquals(recipeEntity.protein, mapped.protein)
        assertEquals(recipeEntity.fat, mapped.fat)
        assertEquals(recipeEntity.servings.toInt(), mapped.servings)
        assertEquals(recipeEntity.instructions, mapped.instructions)
        assertEquals(recipeEntity.visibility, mapped.visibility)
        assertEquals(0, mapped.ingredients.size)
    }
}
