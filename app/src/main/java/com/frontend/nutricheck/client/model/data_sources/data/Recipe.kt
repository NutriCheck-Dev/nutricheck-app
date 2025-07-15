package com.frontend.nutricheck.client.model.data_sources.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey override val id: String = "",
    override val name: String = "Gericht",
    override val calories: Double = 0.0,
    override val carbohydrates: Double = 0.0,
    override val protein: Double = 0.0,
    override val fat: Double = 0.0,
    override val servings: Int = 1,
    val ingredients: Set<Ingredient> = emptySet(),
    val description: String = "Dies ist eine Beispielbeschreibung für ein Rezept. Hier können Details zum Zubereitungsvorgang, den Zutaten und anderen wichtigen Informationen stehen.",
    val visibility: RecipeVisibility = RecipeVisibility.OWNER,
    val report: RecipeReport = RecipeReport()
) : FoodComponent
{
    fun changeName(newName: String): Recipe {
        return this.copy(name = newName)
    }

    fun changeServings(newServings: Int): Recipe {
        return this.copy(servings = newServings)
    }

    fun addIngredient(ingredient: Ingredient): Recipe {
        val updatedRecipe = this.copy(ingredients = this.ingredients + ingredient)
        return updatedRecipe.calculateNutritionalValues()
    }

    fun removeIngredient(ingredient: Ingredient): Recipe {
        val updatedRecipe = this.copy(ingredients = this.ingredients - ingredient)
        return updatedRecipe.calculateNutritionalValues()
    }

    fun changeDescription(newDescription: String): Recipe {
        return this.copy(description = newDescription)
    }

    fun addReport(newReport: RecipeReport): Recipe {
        return this.copy(report = newReport)
    }
    fun calculateNutritionalValues(): Recipe {
        val totalCalories = ingredients.sumOf { it.foodProduct.calories }
        val totalCarbohydrates = ingredients.sumOf { it.foodProduct.carbohydrates }
        val totalProtein = ingredients.sumOf { it.foodProduct.protein }
        val totalFat = ingredients.sumOf { it.foodProduct.fat }

        return this.copy(
            calories = totalCalories,
            carbohydrates = totalCarbohydrates,
            protein = totalProtein,
            fat = totalFat
        )
    }
}
