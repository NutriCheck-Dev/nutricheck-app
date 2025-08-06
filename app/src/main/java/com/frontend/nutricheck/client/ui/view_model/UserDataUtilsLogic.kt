package com.frontend.nutricheck.client.ui.view_model

import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.flags.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.flags.Gender
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.frontend.nutricheck.client.model.data_sources.data.flags.WeightGoal
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.Date
import kotlin.math.roundToInt

/**
 * A utility object containing logic for validating and processing user data.
 * Used in [ProfileViewModel] and [OnboardingViewModel].
 */
object UserDataUtilsLogic {
    /**
    * Validates the user's name.
    * @param name The name to validate.
    * @return A string resource ID for an error message if the name is invalid, otherwise null.
    */
    fun isNameInvalid(name: String): Int? {
        return when {
        name.isBlank() -> R.string.userData_error_name_required
        name.length < 2 -> R.string.userData_error_name_too_short
        name.length > 30 -> R.string.userData_error_name_too_long
        else -> null
        }
    }
    /**
     * Validates the user's birthdate.
     * @param birthdate The birthdate to validate.
     * @return A string resource ID for an error message if the birthdate is invalid, otherwise null.
     */
    fun isBirthdateInvalid(birthdate: Date?): Int? {
        if (birthdate == null) {
            return R.string.userData_error_birthdate_required
        }
        val localBirthdate = Instant.ofEpochMilli(birthdate.time)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val today = LocalDate.now()
        val hundredYearsAgo = today.minusYears(100)
        return when {
            localBirthdate.isAfter(today) -> R.string.userData_error_birthdate_future
            localBirthdate.isBefore(hundredYearsAgo) ->
                R.string.userData_error_birthdate_too_old
            else -> null
        }
    }
    /**
     * Validates the user's height.
     * @param height The height in cm to validate.
     * @return A string resource ID for an error message if the height is invalid, otherwise null.
     */
    fun isHeightInvalid(height: Double?): Int? {
        return when {
            height == null -> R.string.userData_error_height_required
            height < 50.0 -> R.string.userData_error_height_too_short
            height > 250.0 -> R.string.userData_error_height_too_tall
            else -> null
        }
    }
    /**
     * Validates the user's weight.
     * @param weight The weight in kg to validate.
     * @return A string resource ID for an error message if the weight is invalid, otherwise null.
     */
    fun isWeightInvalid(weight: Double?): Int? {
        return when {
            weight == null -> R.string.userData_error_weight_required
            weight < 20.0 -> R.string.userData_error_weight_too_low
            weight > 500.0 -> R.string.userData_error_weight_too_high
            else -> null
        }
    }
    /**
     * Validates the user's target weight.
     * @param targetWeight The target weight in kg to validate.
     * @return A string resource ID for an error message if the target weight is invalid, otherwise null.
     */
    fun isTargetWeightInvalid(targetWeight: Double?): Int? {
        return when {
            targetWeight == null -> R.string.userData_error_target_weight_required
            targetWeight < 20.0 -> R.string.userData_error_target_weight_too_low
            targetWeight > 500.0 -> R.string.userData_error_target_weight_too_high
            else -> null
        }
    }
    /**
     * Calculates the age of a user based on their birthdate.
     * @param birthdate The user's date of birth.
     * @return The user's age in years.
     */
    fun calculateAge(birthdate:  Date) : Int {
        val localBirthdate = Instant.ofEpochMilli(birthdate.time)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val today = LocalDate.now()
        val period = Period.between(localBirthdate, today)
        return period.years
    }
    /**
     * Calculates nutritional goals based on user data.
     * This includes daily calories, protein, carbs, and fats, calculated using the
     * Mifflin-St Jeor equation for Basal Metabolic Rate (BMR) and the Physical Activity Level (PAL).
     * @param userData The complete user data.
     * @return An updated [UserData] object with the calculated nutritional goals.
     */
    fun calculateNutrition(userData : UserData) : UserData {
        val bmr = if (userData.gender == Gender.MALE) {
            10 * userData.weight + (6.25 * userData.height) - (5 * userData.age) + 5
        } else {
            10 * userData.weight + (6.25 * userData.height) - (5 * userData.age) - 161
        }
        val pal = when (userData.activityLevel) {
            ActivityLevel.NEVER -> 1.3
            ActivityLevel.OCCASIONALLY -> 1.6
            ActivityLevel.REGULARLY -> 1.9
            ActivityLevel.FREQUENTLY -> 2.2
        }
        val calorieDiffOnGoal = when (userData.weightGoal) {
            WeightGoal.LOSE_WEIGHT -> -500
            WeightGoal.MAINTAIN_WEIGHT -> 0
            WeightGoal.GAIN_WEIGHT -> 500
        }
        val newDailyCalories = (bmr * pal + calorieDiffOnGoal).roundToInt()
        val newProtein = (userData.weight * 1.8).roundToInt()
        val newFats = (newDailyCalories * 0.25 / 9.3).roundToInt()
        val newCarbs = ((newDailyCalories
                - (newProtein * 4.1 + newFats * 9.3)) / 4.1).roundToInt()
        val newUserData = userData.copy(
            dailyCaloriesGoal = newDailyCalories,
            proteinGoal = newProtein,
            carbsGoal = newCarbs,
            fatsGoal = newFats
        )
        return newUserData
    }
}