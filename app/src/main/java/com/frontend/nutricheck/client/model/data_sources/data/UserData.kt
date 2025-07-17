package com.frontend.nutricheck.client.model.data_sources.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "user_data")
data class UserData(
    @PrimaryKey val username: String = "",
    val birthdate: Date = Date(),
    val gender: Gender = Gender.DIVERS,
    val height: Double? = 0.0,
    val weight: Double? = 0.0,
    val age : Int = 0,
    val targetCalories: Double? = 0.0,
    val weightGoal: WeightGoal = WeightGoal.MAINTAIN_WEIGHT,
    val targetWeight: Double? = 0.0,
    val activityLevel: ActivityLevel = ActivityLevel.NEVER,
    val language: String = "de",
    val theme: String = "light"
) {
    fun changeUsername(newUsername: String): UserData {
        return this.copy(username = newUsername)
    }
    fun changeBirthdate(birthdate: Date): UserData {
        return this.copy(birthdate = birthdate)
    }
    fun changeGender(newGender: Gender): UserData {
        return this.copy(gender = newGender)
    }

    fun changeHeight(newHeight: Double): UserData {
        return this.copy(height = newHeight)
    }
    fun changeLanguage(newLanguage: String): UserData {
        return this.copy(language = newLanguage)
    }
    fun changeTheme(newTheme: String): UserData {
        return this.copy(theme = newTheme)
    }
    fun changeTargetCalories(newTargetCalories: Double): UserData {
        return this.copy(targetCalories = newTargetCalories)
    }
    fun changeWeightGoal(newWeightGoal: WeightGoal): UserData {
        return this.copy(weightGoal = newWeightGoal)
    }
    fun changeTargetWeight(newTargetWeight: Double): UserData {
        return this.copy(targetWeight = newTargetWeight)
    }
    fun changeActivityLevel(newActivityLevel: ActivityLevel): UserData {
        return this.copy(activityLevel = newActivityLevel)
    }
}
