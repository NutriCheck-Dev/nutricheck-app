package com.frontend.nutricheck.client.model.data_sources.persistence.relations

import com.frontend.nutricheck.client.model.data_sources.data.flags.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.model.data_sources.data.flags.Gender
import com.frontend.nutricheck.client.model.data_sources.data.flags.RecipeVisibility
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize
import com.frontend.nutricheck.client.model.data_sources.data.flags.WeightGoal
import com.frontend.nutricheck.client.model.data_sources.persistence.Converters
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.Test
import org.junit.Before
import org.junit.After
import org.junit.Assert.*
import java.util.Calendar
import java.util.Date

/**
 * Unit test class for the Converters class.
 * Tests all TypeConverter methods for proper conversion between database types and domain objects.
 */
class ConvertersTest {

    private lateinit var converters: Converters
    private val mockCalendar = mockk<Calendar>()

    @Before
    fun setUp() {
        converters = Converters()
        mockkStatic(Calendar::class)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    /**
     * Tests Date to Long conversion.
     * Verifies that a Date is properly converted to a timestamp with normalized time components.
     */
    @Test
    fun `fromDate should convert Date to normalized timestamp`() {
        // Given
        val testDate = Date()
        val expectedTimestamp = 1640995200000L // Example timestamp

        every { Calendar.getInstance() } returns mockCalendar
        every { mockCalendar.time = any() } returns Unit
        every { mockCalendar.set(Calendar.HOUR_OF_DAY, 0) } returns Unit
        every { mockCalendar.set(Calendar.MINUTE, 0) } returns Unit
        every { mockCalendar.set(Calendar.SECOND, 0) } returns Unit
        every { mockCalendar.set(Calendar.MILLISECOND, 0) } returns Unit
        every { mockCalendar.timeInMillis } returns expectedTimestamp

        // When
        val result = converters.fromDate(testDate)

        // Then
        assertEquals(expectedTimestamp, result)
    }

    /**
     * Tests Long to Date conversion.
     * Verifies that a timestamp is properly converted to a Date with normalized time components.
     */
    @Test
    fun `toDate should convert timestamp to normalized Date`() {
        // Given
        val timestamp = 1640995200000L
        val expectedDate = Date(timestamp)

        every { Calendar.getInstance() } returns mockCalendar
        every { mockCalendar.timeInMillis = timestamp } returns Unit
        every { mockCalendar.set(Calendar.HOUR_OF_DAY, 0) } returns Unit
        every { mockCalendar.set(Calendar.MINUTE, 0) } returns Unit
        every { mockCalendar.set(Calendar.SECOND, 0) } returns Unit
        every { mockCalendar.set(Calendar.MILLISECOND, 0) } returns Unit
        every { mockCalendar.time } returns expectedDate

        // When
        val result = converters.toDate(timestamp)

        // Then
        assertEquals(expectedDate, result)
    }

    /**
     * Tests Long to Date conversion with null input.
     */
    @Test
    fun `toDate should return null when timestamp is null`() {
        // When
        val result = converters.toDate(null)

        // Then
        assertNull(result)
    }

    /**
     * Tests Gender enum to String conversion.
     */
    @Test
    fun `fromGender should convert Gender to String`() {
        // Given
        val gender = Gender.MALE

        // When
        val result = converters.fromGender(gender)

        // Then
        assertEquals("MALE", result)
    }

    /**
     * Tests Gender enum to String conversion with null input.
     */
    @Test
    fun `fromGender should return null when gender is null`() {
        // When
        val result = converters.fromGender(null)

        // Then
        assertNull(result)
    }

    /**
     * Tests String to Gender enum conversion.
     */
    @Test
    fun `toGender should convert String to Gender`() {
        // Given
        val genderName = "FEMALE"

        // When
        val result = converters.toGender(genderName)

        // Then
        assertEquals(Gender.FEMALE, result)
    }

    /**
     * Tests String to Gender enum conversion with null input.
     */
    @Test
    fun `toGender should return null when name is null`() {
        // When
        val result = converters.toGender(null)

        // Then
        assertNull(result)
    }

    /**
     * Tests ActivityLevel enum to String conversion.
     */
    @Test
    fun `fromActivityLevel should convert ActivityLevel to String`() {
        // Given
        val activityLevel = ActivityLevel.REGULARLY

        // When
        val result = converters.fromActivityLevel(activityLevel)

        // Then
        assertEquals("REGULARLY", result)
    }

    /**
     * Tests ActivityLevel enum to String conversion with null input.
     */
    @Test
    fun `fromActivityLevel should return null when level is null`() {
        // When
        val result = converters.fromActivityLevel(null)

        // Then
        assertNull(result)
    }

    /**
     * Tests String to ActivityLevel enum conversion.
     */
    @Test
    fun `toActivityLevel should convert String to ActivityLevel`() {
        // Given
        val levelName = "FREQUENTLY"

        // When
        val result = converters.toActivityLevel(levelName)

        // Then
        assertEquals(ActivityLevel.FREQUENTLY, result)
    }

    /**
     * Tests String to ActivityLevel enum conversion with null input.
     */
    @Test
    fun `toActivityLevel should return null when name is null`() {
        // When
        val result = converters.toActivityLevel(null)

        // Then
        assertNull(result)
    }

    /**
     * Tests WeightGoal enum to String conversion.
     */
    @Test
    fun `fromWeightGoal should convert WeightGoal to String`() {
        // Given
        val weightGoal = WeightGoal.LOSE_WEIGHT

        // When
        val result = converters.fromWeightGoal(weightGoal)

        // Then
        assertEquals("LOSE_WEIGHT", result)
    }

    /**
     * Tests WeightGoal enum to String conversion with null input.
     */
    @Test
    fun `fromWeightGoal should return null when goal is null`() {
        // When
        val result = converters.fromWeightGoal(null)

        // Then
        assertNull(result)
    }

    /**
     * Tests String to WeightGoal enum conversion.
     */
    @Test
    fun `toWeightGoal should convert String to WeightGoal`() {
        // Given
        val goalName = "GAIN_WEIGHT"

        // When
        val result = converters.toWeightGoal(goalName)

        // Then
        assertEquals(WeightGoal.GAIN_WEIGHT, result)
    }

    /**
     * Tests String to WeightGoal enum conversion with null input.
     */
    @Test
    fun `toWeightGoal should return null when name is null`() {
        // When
        val result = converters.toWeightGoal(null)

        // Then
        assertNull(result)
    }

    /**
     * Tests RecipeVisibility enum to String conversion.
     */
    @Test
    fun `fromRecipeVisibility should convert RecipeVisibility to String`() {
        // Given
        val visibility = RecipeVisibility.PUBLIC

        // When
        val result = converters.fromRecipeVisibility(visibility)

        // Then
        assertEquals("PUBLIC", result)
    }

    /**
     * Tests RecipeVisibility enum to String conversion with null input.
     */
    @Test
    fun `fromRecipeVisibility should return null when visibility is null`() {
        // When
        val result = converters.fromRecipeVisibility(null)

        // Then
        assertNull(result)
    }

    /**
     * Tests String to RecipeVisibility enum conversion.
     */
    @Test
    fun `toRecipeVisibility should convert String to RecipeVisibility`() {
        // Given
        val visibilityName = "OWNER"

        // When
        val result = converters.toRecipeVisibility(visibilityName)

        // Then
        assertEquals(RecipeVisibility.OWNER, result)
    }

    /**
     * Tests String to RecipeVisibility enum conversion with null input.
     */
    @Test
    fun `toRecipeVisibility should return null when name is null`() {
        // When
        val result = converters.toRecipeVisibility(null)

        // Then
        assertNull(result)
    }

    /**
     * Tests ServingSize enum to Int conversion.
     */
    @Test
    fun `fromServingSize should convert ServingSize to ordinal`() {
        // Given
        val servingSize = ServingSize.ONEHOUNDREDGRAMS

        // When
        val result = converters.fromServingSize(servingSize)

        // Then
        assertEquals(ServingSize.ONEHOUNDREDGRAMS.ordinal, result)
    }

    /**
     * Tests ServingSize enum to Int conversion with null input.
     */
    @Test
    fun `fromServingSize should return null when servingSize is null`() {
        // When
        val result = converters.fromServingSize(null)

        // Then
        assertNull(result)
    }

    /**
     * Tests Int to ServingSize enum conversion.
     */
    @Test
    fun `toServingSize should convert ordinal to ServingSize`() {
        // Given
        val ordinal = ServingSize.TENGRAMS.ordinal

        // When
        val result = converters.toServingSize(ordinal)

        // Then
        assertEquals(ServingSize.TENGRAMS, result)
    }

    /**
     * Tests Int to ServingSize enum conversion with null input.
     */
    @Test
    fun `toServingSize should return null when ordinal is null`() {
        // When
        val result = converters.toServingSize(null)

        // Then
        assertNull(result)
    }

    /**
     * Tests Int to ServingSize enum conversion with invalid ordinal.
     */
    @Test
    fun `toServingSize should return null when ordinal is invalid`() {
        // Given
        val invalidOrdinal = 999

        // When
        val result = converters.toServingSize(invalidOrdinal)

        // Then
        assertNull(result)
    }

    /**
     * Tests DayTime enum to String conversion.
     */
    @Test
    fun `fromDayTime should convert DayTime to String`() {
        // Given
        val dayTime = DayTime.BREAKFAST

        // When
        val result = converters.fromDayTime(dayTime)

        // Then
        assertEquals("BREAKFAST", result)
    }

    /**
     * Tests DayTime enum to String conversion with null input.
     */
    @Test
    fun `fromDayTime should return null when dayTime is null`() {
        // When
        val result = converters.fromDayTime(null)

        // Then
        assertNull(result)
    }

    /**
     * Tests String to DayTime enum conversion.
     */
    @Test
    fun `toDayTime should convert String to DayTime`() {
        // Given
        val dayTimeName = "DINNER"

        // When
        val result = converters.toDayTime(dayTimeName)

        // Then
        assertEquals(DayTime.DINNER, result)
    }

    /**
     * Tests String to DayTime enum conversion with null input.
     */
    @Test
    fun `toDayTime should return null when name is null`() {
        // When
        val result = converters.toDayTime(null)

        // Then
        assertNull(result)
    }

    /**
     * Tests all enum conversions for completeness.
     */
    @Test
    fun `should handle all enum values correctly`() {
        // Test all Gender values
        Gender.values().forEach { gender ->
            val converted = converters.fromGender(gender)
            assertEquals(gender, converters.toGender(converted))
        }

        // Test all ActivityLevel values
        ActivityLevel.values().forEach { level ->
            val converted = converters.fromActivityLevel(level)
            assertEquals(level, converters.toActivityLevel(converted))
        }

        // Test all WeightGoal values
        WeightGoal.values().forEach { goal ->
            val converted = converters.fromWeightGoal(goal)
            assertEquals(goal, converters.toWeightGoal(converted))
        }

        // Test all RecipeVisibility values
        RecipeVisibility.values().forEach { visibility ->
            val converted = converters.fromRecipeVisibility(visibility)
            assertEquals(visibility, converters.toRecipeVisibility(converted))
        }

        // Test all ServingSize values
        ServingSize.values().forEach { servingSize ->
            val converted = converters.fromServingSize(servingSize)
            assertEquals(servingSize, converters.toServingSize(converted))
        }

        // Test all DayTime values
        DayTime.values().forEach { dayTime ->
            val converted = converters.fromDayTime(dayTime)
            assertEquals(dayTime, converters.toDayTime(converted))
        }
    }
}