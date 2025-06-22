package com.frontend.nutricheck.client.model.data_layer

data class Food (override val id: FoodComponentId,
                 override val name: String,
                 override val amountInGrams: Double,
                 override val energyInKcal: Int,
                 override val proteinInGrams: Double,
                 override val carbohydratesInGrams: Double,
                 override val fatInGrams: Double
) : FoodComponent
