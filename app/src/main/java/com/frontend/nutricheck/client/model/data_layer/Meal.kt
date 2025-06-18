package com.frontend.nutricheck.client.model.data_layer

class Meal (val name: String, val energyInKcal: Int, val entries: List<IFoodComponent>) {
    /**
     * Calculates the total energy in kcal for the meal.
     * @return Total energy in kcal.
     */
    fun totalEnergyInKcal(): Int {
        return entries.sumOf { it.energyInKcal }
    }

}