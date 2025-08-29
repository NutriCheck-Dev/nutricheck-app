package com.frontend.nutricheck.client.model.repositories.mapper

import com.frontend.nutricheck.client.ui.view_model.TestDataFactory
import org.junit.Test
import org.junit.Assert.assertEquals

class ReportMapperTest {

    @Test
    fun `toDTO should convert Report to ReportDTO`() {
        val report = TestDataFactory.createDefaultReport()

        val dto = ReportMapper.toDto(report)

        assertEquals(report.description, dto.description)
        assertEquals(report.recipeId, dto.recipeId)
    }

}