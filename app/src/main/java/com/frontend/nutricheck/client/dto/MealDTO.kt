package com.frontend.nutricheck.client.dto

data class MealDTO(
    val calories: Double,
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double,
    val items: Set<MealItemDTO>
)