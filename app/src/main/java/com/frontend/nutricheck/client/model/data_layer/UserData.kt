package com.frontend.nutricheck.client.model.data_layer

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val username: String? = "",
    val age: Int? = 0,
    val gender: Gender? = Gender.DIVERS,
    val weight: Double? = 0.0,
    val height: Double? = 0.0,
    val targetCalories: Int? = 0
)
