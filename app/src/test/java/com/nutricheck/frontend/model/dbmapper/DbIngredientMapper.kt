package com.nutricheck.frontend.model.dbmapper

import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.FoodProductEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.IngredientEntity
import com.frontend.nutricheck.client.model.data_sources.persistence.relations.IngredientWithFoodProduct
import com.frontend.nutricheck.client.model.data_sources.persistence.mapper.DbIngredientMapper
import org.junit.Assert.assertEquals
import org.junit.Test

class DbIngredientMapperTest {

    @Test
    fun `toIngredientEntity should map correctly`() {
        val foodProduct = FoodProduct(
            id = "food123",
            name = "Tomate",
            calories = 18.0,
            carbohydrates = 3.9,
            protein = 0.9,
            fat = 0.2
        )

        val ingredient = Ingredient(
            recipeId = "recipe456",
            foodProduct = foodProduct,
            quantity = 2.5,
            servings = 1,
            servingSize = ServingSize.ONEGRAM,

        )

        val entity = DbIngredientMapper.toIngredientEntity(ingredient)

        assertEquals(ingredient.recipeId, entity.recipeId)
        assertEquals(ingredient.foodProduct.id, entity.foodProductId)
        assertEquals(ingredient.quantity, entity.quantity, 0.001)
    }

    @Test
    fun `toIngredient should map correctly`() {
        val foodEntity = FoodProductEntity(
            id = "food789",
            name = "Karotte",
            calories = 41.0,
            carbohydrates = 10.0,
            protein = 0.9,
            fat = 0.2
        )

        val ingredientEntity = IngredientEntity(
            recipeId = "recipe999",
            foodProductId = "food789",
            quantity = 1.5,
            servings = 1,
            servingSize = ServingSize.ONEGRAM

        )

        val relation = IngredientWithFoodProduct(
            ingredient = ingredientEntity,
            foodProduct = foodEntity
        )

        val result = DbIngredientMapper.toIngredient(relation)

        assertEquals(ingredientEntity.recipeId, result.recipeId)
        assertEquals(ingredientEntity.quantity, result.quantity, 0.001)
        assertEquals(foodEntity.id, result.foodProduct.id)
        assertEquals(foodEntity.name, result.foodProduct.name)
        assertEquals(foodEntity.calories, result.foodProduct.calories, 0.001)
        assertEquals(foodEntity.carbohydrates, result.foodProduct.carbohydrates, 0.001)
        assertEquals(foodEntity.protein, result.foodProduct.protein, 0.001)
        assertEquals(foodEntity.fat, result.foodProduct.fat, 0.001)
    }
}
