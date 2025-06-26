package com.frontend.nutricheck.client.model.data_layer

import androidx.room.Entity

@Entity(tableName = "user_data")
data class UserData(
    val username: String? = "",
    val age: Int? = 0,
    val gender: Gender? = Gender.DIVERS,
    val weight: Weight = Weight(),
    val height: Double? = 0.0,
    val targetCalories: Int? = 0,
    val language: String? = "de",
    val theme: String? = "light"
) {
    fun changeUsername(newUsername: String): UserData {
        return this.copy(username = newUsername)
    }
    fun changeAge(newAge: Int): UserData {
        return this.copy(age = newAge)
    }
    fun changeGender(newGender: Gender): UserData {
        return this.copy(gender = newGender)
    }
    fun changeWeight(newWeight: Weight): UserData {
        return this.copy(weight = newWeight)
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
}
