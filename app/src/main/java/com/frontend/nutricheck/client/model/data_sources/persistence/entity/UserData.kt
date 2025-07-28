package com.frontend.nutricheck.client.model.data_sources.persistence.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.frontend.nutricheck.client.model.data_sources.data.flags.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.flags.Gender
import com.frontend.nutricheck.client.model.data_sources.data.flags.WeightGoal
import java.util.Date

@Entity(tableName = "user_data")
data class UserData(
    @PrimaryKey val username: String = "",
    val birthdate: Date = Date(),
    val gender: Gender = Gender.DIVERS,
    val height: Double = 0.0,
    val weight: Double = 0.0,
    val age : Int = 0,
    val weightGoal: WeightGoal = WeightGoal.MAINTAIN_WEIGHT,
    val targetWeight: Double = 0.0,
    val activityLevel: ActivityLevel = ActivityLevel.NEVER,
    val dailyCaloriesGoal: Int = 0,
    val proteinGoal: Int = 0,
    val carbsGoal: Int = 0,
    val fatsGoal: Int = 0
)