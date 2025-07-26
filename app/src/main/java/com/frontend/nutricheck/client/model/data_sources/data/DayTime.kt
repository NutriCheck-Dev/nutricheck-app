package com.frontend.nutricheck.client.model.data_sources.data

enum class DayTime(private val value: String) {
    BREAKFAST("Breakfast"),
    LUNCH ("Lunch"),
    DINNER ("Dinner"),
    SNACK ("Snack");

    override fun toString(): String {
        return value
    }
}