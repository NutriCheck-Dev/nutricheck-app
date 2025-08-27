package com.frontend.nutricheck.client.ui.view_model.utils

import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.flags.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.flags.Gender
import com.frontend.nutricheck.client.model.data_sources.data.flags.WeightGoal
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.frontend.nutricheck.client.ui.view_model.OnboardingViewModel
import com.frontend.nutricheck.client.ui.view_model.ProfileViewModel
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
        val maxNameLength = 30
        val minNameLength = 2
        return when {
        name.isBlank() -> R.string.userData_error_name_required
        name.length < minNameLength -> R.string.userData_error_name_too_short
        name.length > maxNameLength -> R.string.userData_error_name_too_long
        else -> null
        }
    }
    /**
     * Validates the user's birthdate.
     * @param birthdate The birthdate to validate.
     * @return A string resource ID for an error message if the birthdate is invalid, otherwise null.
     */
    fun isBirthdateInvalid(birthdate: Date?): Int? {
        val maxAge : Long = 100
        if (birthdate == null) {
            return R.string.userData_error_birthdate_required
        }
        val localBirthdate = Instant.ofEpochMilli(birthdate.time)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val today = LocalDate.now()
        val hundredYearsAgo = today.minusYears(maxAge)
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
        val minHeight = 50.0
        val maxHeight = 250.0
        return when {
            height == null -> R.string.userData_error_height_required
            height < minHeight -> R.string.userData_error_height_too_short
            height > maxHeight -> R.string.userData_error_height_too_tall
            else -> null
        }
    }
    /**
     * Validates the user's weight.
     * @param weight The weight in kg to validate.
     * @return A string resource ID for an error message if the weight is invalid, otherwise null.
     */
    fun isWeightInvalid(weight: Double?): Int? {
        val minWeight = 20.0
        val maxWeight = 500.0
        return when {
            weight == null -> R.string.userData_error_weight_required
            weight < minWeight -> R.string.userData_error_weight_too_low
            weight > maxWeight -> R.string.userData_error_weight_too_high
            else -> null
        }
    }
    /**
     * Validates the user's target weight.
     * @param targetWeight The target weight in kg to validate.
     * @return A string resource ID for an error message if the target weight is invalid, otherwise null.
     */
    fun isTargetWeightInvalid(targetWeight: Double?): Int? {
        val minTargetWeight = 20.0
        val maxTargetWeight = 500.0
        return when {
            targetWeight == null -> R.string.userData_error_target_weight_required
            targetWeight < minTargetWeight -> R.string.userData_error_target_weight_too_low
            targetWeight > maxTargetWeight -> R.string.userData_error_target_weight_too_high
            else -> null
        }
    }
    /**
     * Calculates the age of a user based on their birthdate.
     * @param birthdate The user's date of birth.
     * @return The user's age in years.
     */
    fun calculateAge(birthdate: Date) : Int {
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
     * The BMR * PAL is considered as the Total Daily Energy Expenditure (TDEE), to which a calorie
     * deficit or surplus is applied based on the user's weight goal. The Nutritional goals are
     * calculated as follows:
     * - Daily Protein goal: 1.8g per kg of body weight (1g of protein = 4.1 calories)
     * - Daily Fats goal: 25% of daily calories, divided by 9 (1g of fat = 9.3 calories)
     * - Daily Carbs goal: Remaining calories after protein and fats, divided by 4 (1g of carbs = 4.1 calories)
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
            ActivityLevel.NEVER -> 1.2
            ActivityLevel.OCCASIONALLY -> 1.4
            ActivityLevel.REGULARLY -> 1.6
            ActivityLevel.FREQUENTLY -> 1.9
        }
        val calorieDiffOnGoal = when (userData.weightGoal) {
            WeightGoal.LOSE_WEIGHT -> -300
            WeightGoal.MAINTAIN_WEIGHT -> 0
            WeightGoal.GAIN_WEIGHT -> 300
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
    /**
     * Extension function to parse a String to Double, accepting both comma and dot as
     * decimal separators.
     *
     * @param String The string to be converted to Double.
     * @return The parsed Double value, or null if the string cannot be converted.
     */
   fun String.toDoubleOrNullFlexible(): Double? {
        return this.replace(',', '.').toDoubleOrNull()
    }
}