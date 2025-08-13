package com.nutricheck.frontend.model.data

import android.content.Context
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.Date

/**
 * Unit tests for the DayTime enum class.
 *
 * This test class verifies the functionality of:
 * - String resource retrieval for meal descriptions
 * - Time-based meal classification logic
 * - Edge cases for time boundaries
 */
class DayTimeTest {

    /**
     * Tests that getDescription() correctly retrieves localized strings from Android resources.
     * Verifies that each DayTime enum value returns the expected string resource.
     */
    @Test
    fun `getDescription should return correct string from context`() {
        // Given
        val mockContext = mockk<Context>()
        val expectedBreakfast = "Breakfast"
        val expectedLunch = "Lunch"
        val expectedDinner = "Dinner"
        val expectedSnack = "Snack"

        // Mock the context.getString() calls for each string resource
        every { mockContext.getString(R.string.label_breakfast) } returns expectedBreakfast
        every { mockContext.getString(R.string.label_lunch) } returns expectedLunch
        every { mockContext.getString(R.string.label_dinner) } returns expectedDinner
        every { mockContext.getString(R.string.label_snack) } returns expectedSnack

        // When & Then
        assertEquals(expectedBreakfast, DayTime.BREAKFAST.getDescription(mockContext))
        assertEquals(expectedLunch, DayTime.LUNCH.getDescription(mockContext))
        assertEquals(expectedDinner, DayTime.DINNER.getDescription(mockContext))
        assertEquals(expectedSnack, DayTime.SNACK.getDescription(mockContext))

        // Verify that getString was called with correct resource IDs
        verify { mockContext.getString(R.string.label_breakfast) }
        verify { mockContext.getString(R.string.label_lunch) }
        verify { mockContext.getString(R.string.label_dinner) }
        verify { mockContext.getString(R.string.label_snack) }
    }

    /**
     * Tests the dateToDayTime function for breakfast time range (5:00-10:59).
     * Verifies both boundary cases and middle values.
     */
    @Test
    fun `dateToDayTime should return BREAKFAST for morning hours`() {
        // Test boundary cases and middle values for breakfast time (5-10)
        assertEquals(DayTime.BREAKFAST, DayTime.Companion.dateToDayTime(createDateWithHour(5))) // Start boundary
        assertEquals(DayTime.BREAKFAST, DayTime.Companion.dateToDayTime(createDateWithHour(7))) // Middle value
        assertEquals(DayTime.BREAKFAST, DayTime.Companion.dateToDayTime(createDateWithHour(10))) // End boundary
    }

    /**
     * Tests the dateToDayTime function for lunch time range (11:00-15:59).
     * Verifies both boundary cases and middle values.
     */
    @Test
    fun `dateToDayTime should return LUNCH for midday hours`() {
        // Test boundary cases and middle values for lunch time (11-15)
        assertEquals(DayTime.LUNCH, DayTime.Companion.dateToDayTime(createDateWithHour(11))) // Start boundary
        assertEquals(DayTime.LUNCH, DayTime.Companion.dateToDayTime(createDateWithHour(13))) // Middle value
        assertEquals(DayTime.LUNCH, DayTime.Companion.dateToDayTime(createDateWithHour(15))) // End boundary
    }

    /**
     * Tests the dateToDayTime function for dinner time range (16:00-20:59).
     * Verifies both boundary cases and middle values.
     */
    @Test
    fun `dateToDayTime should return DINNER for evening hours`() {
        // Test boundary cases and middle values for dinner time (16-20)
        assertEquals(DayTime.DINNER, DayTime.Companion.dateToDayTime(createDateWithHour(16))) // Start boundary
        assertEquals(DayTime.DINNER, DayTime.Companion.dateToDayTime(createDateWithHour(18))) // Middle value
        assertEquals(DayTime.DINNER, DayTime.Companion.dateToDayTime(createDateWithHour(20))) // End boundary
    }

    /**
     * Tests the dateToDayTime function for snack time ranges.
     * Snack time includes: 00:00-04:59 (late night/early morning) and 21:00-23:59 (late evening).
     * This covers all hours not classified as breakfast, lunch, or dinner.
     */
    @Test
    fun `dateToDayTime should return SNACK for other hours`() {
        // Test early morning hours (0-4) - should be snack time
        assertEquals(DayTime.SNACK, DayTime.Companion.dateToDayTime(createDateWithHour(0))) // Midnight
        assertEquals(DayTime.SNACK, DayTime.Companion.dateToDayTime(createDateWithHour(2))) // Early morning
        assertEquals(DayTime.SNACK, DayTime.Companion.dateToDayTime(createDateWithHour(4))) // Pre-breakfast

        // Test late evening hours (21-23) - should be snack time
        assertEquals(DayTime.SNACK, DayTime.Companion.dateToDayTime(createDateWithHour(21))) // Post-dinner
        assertEquals(DayTime.SNACK, DayTime.Companion.dateToDayTime(createDateWithHour(23))) // Late night
    }

    /**
     * Tests edge cases at time boundaries to ensure correct classification.
     * Verifies that times just before and after meal periods are classified correctly.
     */
    @Test
    fun `dateToDayTime should handle boundary edge cases correctly`() {
        assertEquals(DayTime.SNACK, DayTime.Companion.dateToDayTime(createDateWithHour(4)))
        assertEquals(DayTime.BREAKFAST, DayTime.Companion.dateToDayTime(createDateWithHour(5)))
        assertEquals(DayTime.BREAKFAST, DayTime.Companion.dateToDayTime(createDateWithHour(10)))
        assertEquals(DayTime.LUNCH, DayTime.Companion.dateToDayTime(createDateWithHour(11)))
        assertEquals(DayTime.LUNCH, DayTime.Companion.dateToDayTime(createDateWithHour(15)))
        assertEquals(DayTime.DINNER, DayTime.Companion.dateToDayTime(createDateWithHour(16)))
        assertEquals(DayTime.DINNER, DayTime.Companion.dateToDayTime(createDateWithHour(20)))
        assertEquals(DayTime.SNACK, DayTime.Companion.dateToDayTime(createDateWithHour(21)))
    }

    /**
     * Helper method to create a Date object with a specific hour.
     * This simplifies test setup by allowing easy creation of dates with specific times.
     *
     * @param hour The hour of the day (0-23) to set for the created date
     * @return A Date object set to the specified hour on the current date
     */
    private fun createDateWithHour(hour: Int): Date {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }
}