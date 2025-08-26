package com.frontend.nutricheck.client.model.repositories.mapper

import com.frontend.nutricheck.client.dto.ReportDTO
import com.frontend.nutricheck.client.model.data_sources.data.RecipeReport

/**
 * Mapper for converting [RecipeReport] to [ReportDTO].
 */
object ReportMapper {
    fun toDto(report: RecipeReport): ReportDTO =
        ReportDTO(
            description = report.description,
            recipeId = report.recipeId,
        )
}