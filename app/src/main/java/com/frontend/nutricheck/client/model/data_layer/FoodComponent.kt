package com.frontend.nutricheck.client.model.data_layer

interface FoodComponent {
    /**
     * Returns the id of the food component
     */
    val id: FoodComponentId

    /**
     * Returns the name of the food component.
     */
    val name: String

    /**
     * Returns the amount of the food component in grams.
     */
    val amountInGrams: Double

    /**
     * Returns the energy content of the food component in kilocalories.
     */
    val energyInKcal: Int

    /**
     * Returns the protein content of the food component in grams.
     */
    val proteinInGrams: Double

    /**
     * Returns the carbohydrate content of the food component in grams.
     */
    val carbohydratesInGrams: Double

    /**
     * Returns the fat content of the food component in grams.
     */
    val fatInGrams: Double
}