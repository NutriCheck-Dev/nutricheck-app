package com.frontend.nutricheck.client.model.data_sources.data

data class FoodProduct(
    override val id: String,
    override val name: String,
    override val calories: Double,
    override val carbohydrates: Double,
    override val protein: Double,
    override val fat: Double,
    override val servings: Int = 1,
    val servingSize: ServingSize = ServingSize.ONEHOUNDREDGRAMS
) : FoodComponent