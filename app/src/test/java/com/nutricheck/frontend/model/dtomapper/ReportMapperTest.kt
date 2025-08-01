package com.nutricheck.frontend.model.dtomapper

import com.frontend.nutricheck.client.model.data_sources.data.RecipeReport
import com.frontend.nutricheck.client.model.repositories.mapper.ReportMapper
import org.junit.Assert.assertEquals
import org.junit.Test

class ReportMapperTest {

    @Test
    fun `toDto should map RecipeReport to ReportDTO correctly`() {
        val report = RecipeReport(
            description = "Rezept enthält falsche Angaben.",
            recipeId = "recipe123"
        )

        val dto = ReportMapper.toDto(report)

        assertEquals(report.description, dto.description)
        assertEquals(report.recipeId, dto.recipeId)
    }
}
