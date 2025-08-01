package com.nutricheck.frontend.viewmodels

import com.frontend.nutricheck.client.model.data_sources.data.flags.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.flags.Gender
import com.frontend.nutricheck.client.model.data_sources.data.flags.WeightGoal
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.frontend.nutricheck.client.ui.view_model.UserDataUtilsLogic
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.Date

class UtilsTest {
    @Test
    fun `isBirthdateInvalid with valid birthdate returns false`() {
        // Given
            val validBirthdate = Date(631152000000L) // 1990-01-01
            // When
            val result = UserDataUtilsLogic.isBirthdateInvalid(validBirthdate)
            // Then
            assertThat(result).isFalse()

    }
    @Test
    fun `isBirthdateInvalid with future birthdate returns true`() {
        // Given
            val invalidBirthdate = Date(1893456000000L) // 2030-01-01
            // When
            val result = UserDataUtilsLogic.isBirthdateInvalid(invalidBirthdate)
            // Then
            assertThat(result).isTrue()
    }
    @Test
    fun `isBirthdateInvalid with birthdate older than 100 years returns true`() {
        // Given
            val invalidBirthdate = Date(-4417632000000L) // 1830-01-01
            // When
            val result = UserDataUtilsLogic.isBirthdateInvalid(invalidBirthdate)
            // Then
            assertThat(result).isTrue()
    }

    @Test
    fun `calculateAge with valid birthdate returns correct age`() {
        // Given
        val birthdate = Date(631152000000L) // 1990-01-01
        // When
        val age = UserDataUtilsLogic.calculateAge(birthdate)
        // Then
        assertThat(age).isEqualTo(35) // 2025 - 1990
    }
    @Test
    fun `calculateAge with birthdate in 2025 returns 0`() {
        // Given
        val birthdate = Date(1753737600000L) // 2025-07-29
        // When
        val age = UserDataUtilsLogic.calculateAge(birthdate)
        // Then
        assertThat(age).isEqualTo(0)
    }

        @Test
        fun `calculateNutrition for male with lose weight returns correct values`() {
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
            val result = UserDataUtilsLogic.calculateNutrition(userData)
            // Expected values based on the formula
            assertThat(result.dailyCaloriesGoal).isEqualTo(2633)
            assertThat(result.proteinGoal).isEqualTo(126)
            assertThat(result.fatsGoal).isEqualTo(71)
            assertThat(result.carbsGoal).isEqualTo(355)
        }

        @Test
        fun `calculateNutrition for female with maintain weight returns correct values`() {
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
            val result = UserDataUtilsLogic.calculateNutrition(userData)
            // Expected values based on the formula
            assertThat(result.dailyCaloriesGoal).isEqualTo(1749)
            assertThat(result.proteinGoal).isEqualTo(108)
            assertThat(result.fatsGoal).isEqualTo(47)
            assertThat(result.carbsGoal).isEqualTo(212)
        }
    @Test
    fun `calculateNutrition for divers with lose weight returns correct values`() {
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
        val result = UserDataUtilsLogic.calculateNutrition(userData)
        // Expected values based on the formula
        assertThat(result.dailyCaloriesGoal).isEqualTo(1930)
        assertThat(result.proteinGoal).isEqualTo(99)
        assertThat(result.fatsGoal).isEqualTo(52)
        assertThat(result.carbsGoal).isEqualTo(254)
    }
}

