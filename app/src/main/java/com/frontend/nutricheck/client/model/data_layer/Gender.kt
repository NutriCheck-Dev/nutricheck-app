package com.frontend.nutricheck.client.model.data_layer


enum class Gender(private val displayName: String) {
    MALE("Male"),
    FEMALE("Female"),
    DIVERS("Divers");

    fun getDescription(): String {
        return "Gender: $displayName"
    }
}