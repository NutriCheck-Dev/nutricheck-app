package com.frontend.nutricheck.client.model.data_sources.data

enum class WeightGoal(private val displayName: String) {
    LOSE_WEIGHT("Lose Weight"),
    MAINTAIN_WEIGHT("Maintain Weight"),
    GAIN_WEIGHT("Gain Weight");

    fun getDescription(): String {
        return "Weight Goal: $displayName"
    }
}