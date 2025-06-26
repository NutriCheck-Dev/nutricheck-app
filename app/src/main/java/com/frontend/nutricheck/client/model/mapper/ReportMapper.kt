package com.frontend.nutricheck.client.model.mapper

import com.frontend.nutricheck.client.dto.ReportDTO
import com.frontend.nutricheck.client.model.data_layer.RecipeReport

object ReportMapper {
    fun toDto(report: RecipeReport): ReportDTO =
        ReportDTO(
            description = report.description,
            recipeId = report.recipeId,
            recipeName = report.recipeName,
            recipeInstructions = report.recipeInstructions,
        )
}