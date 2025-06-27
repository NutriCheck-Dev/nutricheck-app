package com.frontend.nutricheck.client.model.repositories.mapper

import com.frontend.nutricheck.client.dto.ReportDTO

object ReportMapper {
    fun toDto(report: ReportDTO): ReportDTO =
        ReportDTO(
            description = report.description,
            recipeId = report.recipeId,
        )
}