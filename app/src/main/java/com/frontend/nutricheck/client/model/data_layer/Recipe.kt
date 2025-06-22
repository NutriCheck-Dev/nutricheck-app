package com.frontend.nutricheck.client.model.data_layer

data class Recipe(override val id: FoodComponentId = FoodComponentId(""),
                  override val name: String = "",
                  override val amountInGrams: Double = 0.0,
                  override val energyInKcal: Int = 0,
                  override val proteinInGrams: Double = 0.0,
                  override val carbohydratesInGrams: Double = 0.0,
                  override val fatInGrams: Double = 0.0,
                  val ingredients: List<FoodComponent> = emptyList(),
                  val description: String? = null,
                  val reports: List<RecipeReport> = emptyList(),
                  val hasBeenReported: Boolean = false,
) : FoodComponent
