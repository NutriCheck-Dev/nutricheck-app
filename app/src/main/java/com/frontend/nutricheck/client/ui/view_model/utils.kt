package com.frontend.nutricheck.client.ui.view_model

import com.frontend.nutricheck.client.model.data_sources.data.flags.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.flags.Gender
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.frontend.nutricheck.client.model.data_sources.data.flags.WeightGoal
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.Date

object Utils {

fun birthdateInvalid(birthdate: Date): Boolean {
    val localBirthdate = Instant.ofEpochMilli(birthdate.time)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    val today = LocalDate.now()
    val hundredYearsAgo = today.minusYears(100)
    return localBirthdate.isAfter(today) || localBirthdate.isBefore(hundredYearsAgo)
}
fun calculateAge(birthdate:  Date) : Int {
    val localBirthdate = Instant.ofEpochMilli(birthdate.time)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    val today = LocalDate.now()
    val period = Period.between(localBirthdate, today)
    return period.years
}
fun calculateNutrition(userData : UserData) : UserData {
    var bmr = if (userData.gender == Gender.MALE) {
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
    val newDailyCalories = (bmr * pal + calorieDiffOnGoal).toInt()
    val newProtein = (userData.weight * 1.8).toInt()
    val newFats = (newDailyCalories * 0.25 / 9.3).toInt()
    val newCarbs = ((newDailyCalories
            - (newProtein * 4.1 + newFats * 9.3)) / 4.1).toInt()
    val newUserData = userData.copy(
        dailyCaloriesGoal = newDailyCalories,
        proteinGoal = newProtein,
        carbsGoal = newCarbs,
        fatsGoal = newFats
    )
    return newUserData
}
}