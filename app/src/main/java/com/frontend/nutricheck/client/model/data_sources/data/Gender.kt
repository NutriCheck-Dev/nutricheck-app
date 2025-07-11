package com.frontend.nutricheck.client.model.data_sources.data

import com.nutricheck.frontend.R


enum class Gender(private val displayName: String) {
    MALE("Male"),
    FEMALE("Female"),
    DIVERS("Divers");

    fun getDescription(): String {
        return "Gender: $displayName"
    }
}