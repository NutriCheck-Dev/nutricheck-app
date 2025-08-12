package com.nutricheck.frontend.viewmodels

import com.frontend.nutricheck.client.model.data_sources.data.flags.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.flags.Gender
import com.frontend.nutricheck.client.model.data_sources.data.flags.WeightGoal
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.frontend.nutricheck.client.ui.view_model.UserDataUtilsLogic
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
}
