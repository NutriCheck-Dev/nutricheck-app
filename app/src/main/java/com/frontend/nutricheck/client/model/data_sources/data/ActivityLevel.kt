package com.frontend.nutricheck.client.model.data_sources.data

enum class ActivityLevel(private val displayName: String) {
    NEVER("Never"),
    OCCASIONALLY("Occasionally"),
    REGULARLY("Regularly"),
    FREQUENTLY("Frequently");

    fun getDescription(): String {
        return "Sport Frequency: $displayName"
    }
}