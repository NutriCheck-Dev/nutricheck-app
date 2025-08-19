package com.frontend.nutricheck.client.model.data_sources.persistence.mapper

import com.nutricheck.frontend.TestDataFactory
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class DbRecipeMapperTest {

    val recipe = TestDataFactory.createDefaultRecipe()
    val recipeEntity = TestDataFactory.createDefaultRecipeEntity()

    @Test
    fun `toEntity should convert Recipe to RecipeEntity`() {
        val entity = DbRecipeMapper.toRecipeEntity(recipe, delete = false)
        assertEquals(recipeEntity.id, entity.id)
        assertEquals(recipeEntity.name, entity.name)
        assertEquals(recipeEntity.calories, entity.calories)
        assertEquals(recipeEntity.carbohydrates, entity.carbohydrates)
        assertEquals(recipeEntity.protein, entity.protein)
        assertEquals(recipeEntity.fat, entity.fat)
        assertEquals(recipeEntity.servings, entity.servings)
        assertEquals(recipeEntity.instructions, entity.instructions)
        assertEquals(recipeEntity.visibility, entity.visibility)
    }

    @Test
    fun `toRecipe should convert RecipeWithIngredients to Recipe`() {
        val recipeWithIngredients = TestDataFactory.createDefaultRecipeWithIngredients()
        val recipeResult = DbRecipeMapper.toRecipe(recipeWithIngredients)
        assertEquals(recipe.id, recipeResult.id)
        assertEquals(recipe.name, recipeResult.name)
        assertEquals(recipe.calories, recipeResult.calories)
        assertEquals(recipe.carbohydrates, recipeResult.carbohydrates)
        assertEquals(recipe.protein, recipeResult.protein)
        assertEquals(recipe.fat, recipeResult.fat)
        assertEquals(recipe.servings, recipeResult.servings)
        assertEquals(recipe.instructions, recipeResult.instructions)
        assertEquals(recipe.visibility, recipeResult.visibility)
        assertEquals(recipe.ingredients.first(), recipeResult.ingredients.first())
    }

}