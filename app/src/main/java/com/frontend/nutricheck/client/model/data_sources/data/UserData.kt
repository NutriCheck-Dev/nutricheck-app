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
)

