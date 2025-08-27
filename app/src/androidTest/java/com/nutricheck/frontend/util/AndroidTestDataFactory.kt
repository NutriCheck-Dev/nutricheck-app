package com.nutricheck.frontend.util

import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize

object AndroidTestDataFactory {

    fun ownerRecipeFactory(
        name: String = "Pasta Pesto"
    ) : () -> Recipe = {
        Recipe(
            id = "r1",
            name = name,
            calories = 300.0,
            carbohydrates = 40.0,
            protein = 15.0,
            fat = 10.0,
            servings = 2.0,
            ingredients = listOf(
                Ingredient(
                    recipeId = "r1",
                    foodProduct = FoodProduct(
                        id = "fp1",
                        name = "Pasta",
                        calories = 200.0,
                        carbohydrates = 30.0,
                        protein = 10.0,
                        fat = 5.0,
                        servings = 1.0,
                        servingSize = ServingSize.ONEHOUNDREDGRAMS
                    ),
                    quantity = 100.0,
                    servings = 1.0,
                    servingSize = ServingSize.ONEHOUNDREDGRAMS
                ),
                Ingredient(
                    recipeId = "r1",
                    foodProduct = FoodProduct(
                        id = "fp2",
                        name = "Pesto Sauce",
                        calories = 100.0,
                        carbohydrates = 10.0,
                        protein = 5.0,
                        fat = 5.0,
                        servings = 1.0,
                        servingSize = ServingSize.ONEHOUNDREDGRAMS
                    ),
                    quantity = 50.0,
                    servings = 5.0,
                    servingSize = ServingSize.TENGRAMS
                ),
                Ingredient(
                    recipeId = "r1",
                    foodProduct = FoodProduct(
                        id = "fp3",
                        name = "Olive Oil",
                        calories = 50.0,
                        carbohydrates = 0.0,
                        protein = 0.0,
                        fat = 5.0,
                        servings = 1.0,
                        servingSize = ServingSize.ONEHOUNDREDGRAMS
                    ),
                    quantity = 20.0,
                    servings = 1.0,
                    servingSize = ServingSize.TENGRAMS
                )
            )
        )
    }
}