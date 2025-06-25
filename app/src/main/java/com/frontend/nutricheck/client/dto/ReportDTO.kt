package com.frontend.nutricheck.client.dto

data class ReportDTO(
    val id: String?,
    val description: String?,
    val recipeId: String?,
    val recipeName: String?,
    val recipeInstructions: String?
)