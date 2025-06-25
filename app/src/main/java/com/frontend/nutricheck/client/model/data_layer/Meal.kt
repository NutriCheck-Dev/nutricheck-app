package com.frontend.nutricheck.client.model.data_layer

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlin.collections.plus

@Serializable @Entity(tableName = "meals")
data class Meal (
    @PrimaryKey val id: String = "",
    val name: String,
    val calories: Double = 0.0,
    val carbohydrates: Double = 0.0,
    val protein: Double = 0.0,
    val fat: Double = 0.0,
    private val items: Set<FoodProduct>
) {

    fun addProduct(foodProduct: FoodProduct): Meal {
        val updated = this.copy(items = this.items + foodProduct)
        return updated.calculateNutritionalValues()
    }
    fun removeProduct(foodProduct: FoodProduct): Meal {
        val updated = this.copy(items = this.items.filterNot { it.id == id }.toSet())
        return updated.calculateNutritionalValues()
    }
    fun getComponentById(id: String): FoodComponent? {
        return this.items.find { it.id == id }
    }
    fun calculateNutritionalValues(): Meal {
        val totalCalories = items.sumOf { it.calories }
        val totalProtein = items.sumOf { it.protein }
        val totalCarbohydrates = items.sumOf { it.carbohydrates }
        val totalFat = items.sumOf { it.fat }
        return this.copy(
            calories = totalCalories,
            protein = totalProtein,
            carbohydrates = totalCarbohydrates,
            fat = totalFat
        )
    }
}