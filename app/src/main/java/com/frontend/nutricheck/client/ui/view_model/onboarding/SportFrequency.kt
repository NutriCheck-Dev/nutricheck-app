package com.frontend.nutricheck.client.ui.view_model.onboarding

enum class SportFrequency(val displayName: String) {
    NEVER("Never"),
    OCCASIONALLY("Occasionally"),
    REGULARLY("Regularly"),
    FREQUENTLY("Frequently");

    fun getDescription(): String {
        return "Sport Frequency: $displayName"
    }
}