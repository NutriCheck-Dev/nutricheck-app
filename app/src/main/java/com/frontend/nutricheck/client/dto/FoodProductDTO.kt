package com.frontend.nutricheck.client.dto

data class FoodProductDTO(
    val id: String,
    val name: String,
    val calories: Double,
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double
)