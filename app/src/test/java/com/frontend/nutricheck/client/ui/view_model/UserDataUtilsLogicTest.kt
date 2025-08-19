package com.frontend.nutricheck.client.ui.view_model

import com.frontend.nutricheck.client.model.data_sources.data.flags.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.flags.Gender
import com.frontend.nutricheck.client.model.data_sources.data.flags.WeightGoal
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import java.util.Calendar
import java.util.Date

/**
 * Unit tests for the [UserDataUtilsLogic] class using the MockK framework.
 */
class UserDataUtilsLogicTest {

    private val calendar: Calendar = mockk()
    /**
     * Tests that [UserDataUtilsLogic.isNameInvalid] returns null for a valid name.
     */
    @Test
    fun `isNameInvalid with valid name returns null`() {
        // Given a valid name
        val validName = "John Doe"

        // When the method is called
        val result = UserDataUtilsLogic.isNameInvalid(validName)

        // Then the result should be null
        assertThat(result).isNull()
    }

    /**
     * Tests that [UserDataUtilsLogic.isNameInvalid] returns error for blank name.
     */
    @Test
    fun `isNameInvalid with blank name returns error`() {
        // Given a blank name
        val blankName = ""

        // When the method is called
        val result = UserDataUtilsLogic.isNameInvalid(blankName)

        // Then the result should indicate a required error
        assertThat(result).isNotNull()
    }

    /**
     * Tests that [UserDataUtilsLogic.isNameInvalid] returns error for whitespace-only name.
     */
    @Test
    fun `isNameInvalid with whitespace only name returns error`() {
        // Given a name with only whitespace
        val whitespaceName = "   "

        // When the method is called
        val result = UserDataUtilsLogic.isNameInvalid(whitespaceName)

        // Then the result should indicate a required error
        assertThat(result).isNotNull()
    }

    /**
     * Tests that [UserDataUtilsLogic.isNameInvalid] returns error for name too short.
     */
    @Test
    fun `isNameInvalid with name too short returns error`() {
        // Given a name that is too short (1 character)
        val shortName = "A"

        // When the method is called
        val result = UserDataUtilsLogic.isNameInvalid(shortName)

        // Then the result should indicate a too short error
        assertThat(result).isNotNull()
    }

    /**
     * Tests that [UserDataUtilsLogic.isNameInvalid] returns null for minimum valid name length.
     */
    @Test
    fun `isNameInvalid with minimum valid name length returns null`() {
        // Given a name with exactly 2 characters (minimum)
        val minName = "Al"

        // When the method is called
        val result = UserDataUtilsLogic.isNameInvalid(minName)

        // Then the result should be null
        assertThat(result).isNull()
    }

    /**
     * Tests that [UserDataUtilsLogic.isNameInvalid] returns null for maximum valid name length.
     */
    @Test
    fun `isNameInvalid with maximum valid name length returns null`() {
        // Given a name with exactly 30 characters (maximum)
        val maxName = "A".repeat(30)

        // When the method is called
        val result = UserDataUtilsLogic.isNameInvalid(maxName)

        // Then the result should be null
        assertThat(result).isNull()
    }

    /**
     * Tests that [UserDataUtilsLogic.isNameInvalid] returns error for name too long.
     */
    @Test
    fun `isNameInvalid with name too long returns error`() {
        // Given a name that is too long (31 characters)
        val longName = "A".repeat(31)

        // When the method is called
        val result = UserDataUtilsLogic.isNameInvalid(longName)

        // Then the result should indicate a too long error
        assertThat(result).isNotNull()
    }
    /**
     * Tests that the [UserDataUtilsLogic.isBirthdateInvalid] method returns `null`
     * when a valid birthdate is provided.
     */
    @Test
    fun `isBirthdateInvalid with valid birthdate returns null`() {
        // Given a valid birthdate from 1990
        val validBirthdate = Date(631152000000L) // 1990-01-01

        // When the method is called with the valid birthdate
        val result = UserDataUtilsLogic.isBirthdateInvalid(validBirthdate)

        // Then the result should be null, indicating no error
        assertThat(result).isNull()
    }

    /**
     * Tests that [UserDataUtilsLogic.isBirthdateInvalid] returns error for null birthdate.
     */
    @Test
    fun `isBirthdateInvalid with null birthdate returns error`() {
        // Given a null birthdate
        val nullBirthdate: Date? = null

        // When the method is called
        val result = UserDataUtilsLogic.isBirthdateInvalid(nullBirthdate)

        // Then the result should indicate a required error
        assertThat(result).isNotNull()
    }

    /**
     * Tests that the [UserDataUtilsLogic.isBirthdateInvalid] method returns a non-null
     * value (an error message) when a future birthdate is provided.
     */
    @Test
    fun `isBirthdateInvalid with future birthdate returns non-null`() {
        // Given an invalid birthdate in the future
        val invalidBirthdate = Date(1893456000000L) // 2030-01-01

        // When the method is called with the future birthdate
        val result = UserDataUtilsLogic.isBirthdateInvalid(invalidBirthdate)

        // Then the result should not be null, indicating an error
        assertThat(result).isNotNull()
    }

    /**
     * Tests that the [UserDataUtilsLogic.isBirthdateInvalid] method returns a non-null
     * value (an error message) when a birthdate older than 100 years is provided.
     */
    @Test
    fun `isBirthdateInvalid with birthdate older than 100 years returns non-null`() {
        // Given an invalid birthdate older than 100 years
        val invalidBirthdate = Date(-4417632000000L) // 1830-01-01

        // When the method is called with the old birthdate
        val result = UserDataUtilsLogic.isBirthdateInvalid(invalidBirthdate)

        // Then the result should not be null, indicating an error
        assertThat(result).isNotNull()
    }

    /**
     * Tests boundary case: birthdate exactly 100 years ago should be valid.
     */
    @Test
    fun `isBirthdateInvalid with birthdate exactly 100 years ago returns null`() {
        // Given a birthdate exactly 100 years ago (should be valid)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -100)
        val hundredYearsAgo = calendar.time

        // When the method is called
        val result = UserDataUtilsLogic.isBirthdateInvalid(hundredYearsAgo)

        // Then the result should be null (valid)
        assertThat(result).isNull()
    }
    /**
     * Tests that [UserDataUtilsLogic.isHeightInvalid] returns null for valid height.
     */
    @Test
    fun `isHeightInvalid with valid height returns null`() {
        // Given a valid height
        val validHeight = 175.0

        // When the method is called
        val result = UserDataUtilsLogic.isHeightInvalid(validHeight)

        // Then the result should be null
        assertThat(result).isNull()
    }

    /**
     * Tests that [UserDataUtilsLogic.isHeightInvalid] returns error for null height.
     */
    @Test
    fun `isHeightInvalid with null height returns error`() {
        // Given a null height
        val nullHeight: Double? = null

        // When the method is called
        val result = UserDataUtilsLogic.isHeightInvalid(nullHeight)

        // Then the result should indicate a required error
        assertThat(result).isNotNull()
    }

    /**
     * Tests that [UserDataUtilsLogic.isHeightInvalid] returns error for height too short.
     */
    @Test
    fun `isHeightInvalid with height too short returns error`() {
        // Given a height that is too short
        val shortHeight = 49.0

        // When the method is called
        val result = UserDataUtilsLogic.isHeightInvalid(shortHeight)

        // Then the result should indicate a too short error
        assertThat(result).isNotNull()
    }

    /**
     * Tests that [UserDataUtilsLogic.isHeightInvalid] returns null for minimum valid height.
     */
    @Test
    fun `isHeightInvalid with minimum valid height returns null`() {
        // Given minimum valid height (50.0)
        val minHeight = 50.0

        // When the method is called
        val result = UserDataUtilsLogic.isHeightInvalid(minHeight)

        // Then the result should be null
        assertThat(result).isNull()
    }

    /**
     * Tests that [UserDataUtilsLogic.isHeightInvalid] returns null for maximum valid height.
     */
    @Test
    fun `isHeightInvalid with maximum valid height returns null`() {
        // Given maximum valid height (250.0)
        val maxHeight = 250.0

        // When the method is called
        val result = UserDataUtilsLogic.isHeightInvalid(maxHeight)

        // Then the result should be null
        assertThat(result).isNull()
    }

    /**
     * Tests that [UserDataUtilsLogic.isHeightInvalid] returns error for height too tall.
     */
    @Test
    fun `isHeightInvalid with height too tall returns error`() {
        // Given a height that is too tall
        val tallHeight = 251.0

        // When the method is called
        val result = UserDataUtilsLogic.isHeightInvalid(tallHeight)

        // Then the result should indicate a too tall error
        assertThat(result).isNotNull()
    }
    /**
     * Tests that [UserDataUtilsLogic.isWeightInvalid] returns null for valid weight.
     */
    @Test
    fun `isWeightInvalid with valid weight returns null`() {
        // Given a valid weight
        val validWeight = 70.0

        // When the method is called
        val result = UserDataUtilsLogic.isWeightInvalid(validWeight)

        // Then the result should be null
        assertThat(result).isNull()
    }

    /**
     * Tests that [UserDataUtilsLogic.isWeightInvalid] returns error for null weight.
     */
    @Test
    fun `isWeightInvalid with null weight returns error`() {
        // Given a null weight
        val nullWeight: Double? = null

        // When the method is called
        val result = UserDataUtilsLogic.isWeightInvalid(nullWeight)

        // Then the result should indicate a required error
        assertThat(result).isNotNull()
    }

    /**
     * Tests that [UserDataUtilsLogic.isWeightInvalid] returns error for weight too low.
     */
    @Test
    fun `isWeightInvalid with weight too low returns error`() {
        // Given a weight that is too low
        val lowWeight = 19.0

        // When the method is called
        val result = UserDataUtilsLogic.isWeightInvalid(lowWeight)

        // Then the result should indicate a too low error
        assertThat(result).isNotNull()
    }

    /**
     * Tests that [UserDataUtilsLogic.isWeightInvalid] returns null for minimum valid weight.
     */
    @Test
    fun `isWeightInvalid with minimum valid weight returns null`() {
        // Given minimum valid weight (20.0)
        val minWeight = 20.0

        // When the method is called
        val result = UserDataUtilsLogic.isWeightInvalid(minWeight)

        // Then the result should be null
        assertThat(result).isNull()
    }

    /**
     * Tests that [UserDataUtilsLogic.isWeightInvalid] returns null for maximum valid weight.
     */
    @Test
    fun `isWeightInvalid with maximum valid weight returns null`() {
        // Given maximum valid weight (500.0)
        val maxWeight = 500.0

        // When the method is called
        val result = UserDataUtilsLogic.isWeightInvalid(maxWeight)

        // Then the result should be null
        assertThat(result).isNull()
    }

    /**
     * Tests that [UserDataUtilsLogic.isWeightInvalid] returns error for weight too high.
     */
    @Test
    fun `isWeightInvalid with weight too high returns error`() {
        // Given a weight that is too high
        val highWeight = 501.0

        // When the method is called
        val result = UserDataUtilsLogic.isWeightInvalid(highWeight)

        // Then the result should indicate a too high error
        assertThat(result).isNotNull()
    }

    /**
     * Tests that [UserDataUtilsLogic.isTargetWeightInvalid] returns null for valid target weight.
     */
    @Test
    fun `isTargetWeightInvalid with valid target weight returns null`() {
        // Given a valid target weight
        val validTargetWeight = 65.0

        // When the method is called
        val result = UserDataUtilsLogic.isTargetWeightInvalid(validTargetWeight)

        // Then the result should be null
        assertThat(result).isNull()
    }

    /**
     * Tests that [UserDataUtilsLogic.isTargetWeightInvalid] returns error for null target weight.
     */
    @Test
    fun `isTargetWeightInvalid with null target weight returns error`() {
        // Given a null target weight
        val nullTargetWeight: Double? = null

        // When the method is called
        val result = UserDataUtilsLogic.isTargetWeightInvalid(nullTargetWeight)

        // Then the result should indicate a required error
        assertThat(result).isNotNull()
    }

    /**
     * Tests that [UserDataUtilsLogic.isTargetWeightInvalid] returns error for target weight too low.
     */
    @Test
    fun `isTargetWeightInvalid with target weight too low returns error`() {
        // Given a target weight that is too low
        val lowTargetWeight = 19.0

        // When the method is called
        val result = UserDataUtilsLogic.isTargetWeightInvalid(lowTargetWeight)

        // Then the result should indicate a too low error
        assertThat(result).isNotNull()
    }

    /**
     * Tests that [UserDataUtilsLogic.isTargetWeightInvalid] returns null for minimum valid target weight.
     */
    @Test
    fun `isTargetWeightInvalid with minimum valid target weight returns null`() {
        // Given minimum valid target weight (20.0)
        val minTargetWeight = 20.0

        // When the method is called
        val result = UserDataUtilsLogic.isTargetWeightInvalid(minTargetWeight)

        // Then the result should be null
        assertThat(result).isNull()
    }

    /**
     * Tests that [UserDataUtilsLogic.isTargetWeightInvalid] returns null for maximum valid target weight.
     */
    @Test
    fun `isTargetWeightInvalid with maximum valid target weight returns null`() {
        // Given maximum valid target weight (500.0)
        val maxTargetWeight = 500.0

        // When the method is called
        val result = UserDataUtilsLogic.isTargetWeightInvalid(maxTargetWeight)

        // Then the result should be null
        assertThat(result).isNull()
    }

    /**
     * Tests that [UserDataUtilsLogic.isTargetWeightInvalid] returns error for target weight too high.
     */
    @Test
    fun `isTargetWeightInvalid with target weight too high returns error`() {
        // Given a target weight that is too high
        val highTargetWeight = 501.0

        // When the method is called
        val result = UserDataUtilsLogic.isTargetWeightInvalid(highTargetWeight)

        // Then the result should indicate a too high error
        assertThat(result).isNotNull()
    }

    /**
     * Tests that the [UserDataUtilsLogic.calculateAge] method returns the correct
     * age for a given valid birthdate by mocking the current year.
     */
    @Test
    fun `calculateAge with valid birthdate returns correct age`() {
        // Given a birthdate from 1990 and a mocked current year of 2025
        val birthdate = Date(631152000000L) // 1990-01-01
        every { calendar.get(Calendar.YEAR) } returns 2025

        // When the age is calculated
        val age = UserDataUtilsLogic.calculateAge(birthdate)

        // Then the age should be the difference between the years
        assertThat(age).isEqualTo(35)
    }

    /**
     * Tests that the [UserDataUtilsLogic.calculateAge] method returns 0 for a birthdate
     * within the current year.
     */
    @Test
    fun `calculateAge with birthdate in current year returns 0`() {
        // Given a birthdate in the current year
        val birthdate = Date(1753737600000L) // 2025-07-29

        // When the age is calculated
        val age = UserDataUtilsLogic.calculateAge(birthdate)

        // Then the age should be 0
        assertThat(age).isEqualTo(0)
    }

    /**
     * Tests age calculation for someone born today.
     */
    @Test
    fun `calculateAge with birthdate today returns 0`() {
        // Given a birthdate today
        val today = Date()

        // When the age is calculated
        val age = UserDataUtilsLogic.calculateAge(today)

        // Then the age should be 0
        assertThat(age).isEqualTo(0)
    }

    /**
     * Tests age calculation for edge case: birthday hasn't occurred this year yet.
     */
    @Test
    fun `calculateAge with birthday not yet occurred this year returns correct age`() {
        // Given a birthdate from 1990 but later in the year than current date
        val calendar = Calendar.getInstance()
        calendar.set(1990, Calendar.DECEMBER, 31) // December 31, 1990
        val birthdate = calendar.time

        // When the age is calculated (assuming current date is before December 31)
        val age = UserDataUtilsLogic.calculateAge(birthdate)

        // Then the age should account for birthday not having occurred
        assertThat(age).isAtLeast(34) // Should be either 34 or 35 depending on current date
    }
    /**
     * Tests that the [UserDataUtilsLogic.calculateNutrition] method returns the
     * correct nutritional goals for a male user aiming to lose weight.
     */
    @Test
    fun `calculateNutrition for male with lose weight returns correct values`() {
        // Given a UserData object for a male user with a weight loss goal
        val userData = UserData(
            username = "TestUser",
            birthdate = Date(),
            gender = Gender.MALE,
            height = 175.0,
            weight = 70.0,
            targetWeight = 65.0,
            activityLevel = ActivityLevel.REGULARLY,
            weightGoal = WeightGoal.LOSE_WEIGHT,
            age = 30,
            dailyCaloriesGoal = 0,
            proteinGoal = 0,
            carbsGoal = 0,
            fatsGoal = 0
        )

        // When the nutrition is calculated
        val result = UserDataUtilsLogic.calculateNutrition(userData)

        // Then the results should match the expected values based on the formula
        assertThat(result.dailyCaloriesGoal).isEqualTo(2633)
        assertThat(result.proteinGoal).isEqualTo(126)
        assertThat(result.fatsGoal).isEqualTo(71)
        assertThat(result.carbsGoal).isEqualTo(355)
    }

    /**
     * Tests that the [UserDataUtilsLogic.calculateNutrition] method returns the
     * correct nutritional goals for a female user aiming to maintain weight.
     */
    @Test
    fun `calculateNutrition for female with maintain weight returns correct values`() {
        // Given a UserData object for a female user with a weight maintenance goal
        val userData = UserData(
            username = "TestUser",
            birthdate = Date(),
            gender = Gender.FEMALE,
            height = 165.0,
            weight = 60.0,
            targetWeight = 60.0,
            activityLevel = ActivityLevel.NEVER,
            weightGoal = WeightGoal.MAINTAIN_WEIGHT,
            age = 25,
            dailyCaloriesGoal = 0,
            proteinGoal = 0,
            carbsGoal = 0,
            fatsGoal = 0
        )

        // When the nutrition is calculated
        val result = UserDataUtilsLogic.calculateNutrition(userData)

        // Then the results should match the expected values based on the formula
        assertThat(result.dailyCaloriesGoal).isEqualTo(1749)
        assertThat(result.proteinGoal).isEqualTo(108)
        assertThat(result.fatsGoal).isEqualTo(47)
        assertThat(result.carbsGoal).isEqualTo(212)
    }

    /**
     * Tests that the [UserDataUtilsLogic.calculateNutrition] method returns the
     * correct nutritional goals for a female user aiming to lose weight
     * with a different set of input values.
     */
    @Test
    fun `calculateNutrition for female with lose weight returns correct values`() {
        // Given a UserData object for a female user with a different weight loss goal
        val userData = UserData(
            username = "TestUser",
            birthdate = Date(),
            gender = Gender.FEMALE,
            height = 160.0,
            weight = 55.0,
            targetWeight = 50.0,
            activityLevel = ActivityLevel.REGULARLY,
            weightGoal = WeightGoal.LOSE_WEIGHT,
            age = 22,
            dailyCaloriesGoal = 0,
            proteinGoal = 0,
            carbsGoal = 0,
            fatsGoal = 0
        )

        // When the nutrition is calculated
        val result = UserDataUtilsLogic.calculateNutrition(userData)

        // Then the results should match the expected values based on the formula
        assertThat(result.dailyCaloriesGoal).isEqualTo(1930)
        assertThat(result.proteinGoal).isEqualTo(99)
        assertThat(result.fatsGoal).isEqualTo(52)
        assertThat(result.carbsGoal).isEqualTo(254)
    }

    /**
     * Tests nutrition calculation for male user aiming to gain weight.
     */
    @Test
    fun `calculateNutrition for male with gain weight returns correct values`() {
        // Given a UserData object for a male user with weight gain goal
        val userData = UserData(
            username = "TestUser",
            birthdate = Date(),
            gender = Gender.MALE,
            height = 180.0,
            weight = 65.0,
            targetWeight = 75.0,
            activityLevel = ActivityLevel.OCCASIONALLY,
            weightGoal = WeightGoal.GAIN_WEIGHT,
            age = 25,
            dailyCaloriesGoal = 0,
            proteinGoal = 0,
            carbsGoal = 0,
            fatsGoal = 0
        )

        // When the nutrition is calculated
        val result = UserDataUtilsLogic.calculateNutrition(userData)

        // Then the results should be calculated with +500 calorie surplus
        assertThat(result.dailyCaloriesGoal).isGreaterThan(2500) // Should include surplus
        assertThat(result.proteinGoal).isEqualTo(117) // 65 * 1.8 = 117
        assertThat(result.fatsGoal).isGreaterThan(0)
        assertThat(result.carbsGoal).isGreaterThan(0)
    }

    /**
     * Tests nutrition calculation for female user with FREQUENTLY activity level.
     */
    @Test
    fun `calculateNutrition for female with frequently activity level returns correct values`() {
        // Given a UserData object for a female user with frequent activity
        val userData = UserData(
            username = "TestUser",
            birthdate = Date(),
            gender = Gender.FEMALE,
            height = 170.0,
            weight = 65.0,
            targetWeight = 65.0,
            activityLevel = ActivityLevel.FREQUENTLY,
            weightGoal = WeightGoal.MAINTAIN_WEIGHT,
            age = 28,
            dailyCaloriesGoal = 0,
            proteinGoal = 0,
            carbsGoal = 0,
            fatsGoal = 0
        )

        // When the nutrition is calculated
        val result = UserDataUtilsLogic.calculateNutrition(userData)

        // Then the results should reflect high activity level (PAL = 2.2)
        assertThat(result.dailyCaloriesGoal).isGreaterThan(2200) // High activity should give more calories
        assertThat(result.proteinGoal).isEqualTo(117) // 65 * 1.8 = 117
        assertThat(result.fatsGoal).isGreaterThan(0)
        assertThat(result.carbsGoal).isGreaterThan(0)
    }

    /**
     * Tests nutrition calculation for all activity levels to ensure PAL is applied correctly.
     */
    @Test
    fun `calculateNutrition with different activity levels produces different calories`() {
        // Given base user data
        val baseUserData = UserData(
            username = "TestUser",
            birthdate = Date(),
            gender = Gender.MALE,
            height = 175.0,
            weight = 70.0,
            targetWeight = 70.0,
            activityLevel = ActivityLevel.NEVER,
            weightGoal = WeightGoal.MAINTAIN_WEIGHT,
            age = 30,
            dailyCaloriesGoal = 0,
            proteinGoal = 0,
            carbsGoal = 0,
            fatsGoal = 0
        )

        // When nutrition is calculated for different activity levels
        val neverActive = UserDataUtilsLogic.calculateNutrition(
            baseUserData.copy(activityLevel = ActivityLevel.NEVER)
        )
        val occasionallyActive = UserDataUtilsLogic.calculateNutrition(
            baseUserData.copy(activityLevel = ActivityLevel.OCCASIONALLY)
        )
        val regularlyActive = UserDataUtilsLogic.calculateNutrition(
            baseUserData.copy(activityLevel = ActivityLevel.REGULARLY)
        )
        val frequentlyActive = UserDataUtilsLogic.calculateNutrition(
            baseUserData.copy(activityLevel = ActivityLevel.FREQUENTLY)
        )

        // Then calories should increase with activity level
        assertThat(neverActive.dailyCaloriesGoal)
            .isLessThan(occasionallyActive.dailyCaloriesGoal)
        assertThat(occasionallyActive.dailyCaloriesGoal)
            .isLessThan(regularlyActive.dailyCaloriesGoal)
        assertThat(regularlyActive.dailyCaloriesGoal)
            .isLessThan(frequentlyActive.dailyCaloriesGoal)
    }
}
