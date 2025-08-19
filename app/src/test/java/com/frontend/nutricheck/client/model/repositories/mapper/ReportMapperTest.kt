package com.frontend.nutricheck.client.model.repositories.mapper

import com.nutricheck.frontend.TestDataFactory
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class ReportMapperTest {

    @Test
    fun `toDTO should convert Report to ReportDTO`() {
        val report = TestDataFactory.createDefaultReport()

        val dto = ReportMapper.toDto(report)

        assertEquals(report.description, dto.description)
        assertEquals(report.recipeId, dto.recipeId)
    }

}