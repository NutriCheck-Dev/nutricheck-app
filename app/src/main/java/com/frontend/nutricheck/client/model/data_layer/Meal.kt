package com.frontend.nutricheck.client.model.data_layer

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable @Entity(tableName = "meals")
data class Meal (
    @PrimaryKey val id: String = "",
    val name: String = "",
    private val entries: List<FoodComponent> = emptyList()
) {
    /**
     * Calculates the total energy in kcal for the meal.
     * @return Total energy in kcal.
     */
    fun totalEnergyInKcal(): Int {
        return entries.sumOf { it.calories }
    }
    /**
     * Returns a new Meal instance with the specified new name.
     * @param newName The new name for the meal.
     * @return A new Meal instance with the updated name.
     */
    fun renameMeal(newName: String): Meal {
        return Meal(id, newName, entries)
    }
    /**
     * Finds and returns a food component by its ID.
     * @param id The ID of the food component to find.
     * @return The food component with the specified ID, or null if not found.
     */
    fun getComponentById(id: String = ""): FoodComponent? {
        return entries.find { it.id == id }
    }

    /**
     * Removes a food component from the meal by its ID and returns a new Meal instance.
     * @param id The ID of the food component to remove.
     * @return A new Meal instance with the specified component removed.
     */
    fun removeComponentById(id: String = ""): Meal {
        val updatedEntries = entries.filterNot { it.id == id }
        return Meal(id, name, updatedEntries)
    }
}