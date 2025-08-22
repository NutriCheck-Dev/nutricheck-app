package com.frontend.nutricheck.client.dto

/**
 * Data Transfer Object for reporting issues related to recipes.
 *
 * @property description A description of the issue being reported.
 * @property recipeId The ID of the recipe that the report is associated with.
 */
data class ReportDTO(
    val description: String?,
    val recipeId: String?,
)