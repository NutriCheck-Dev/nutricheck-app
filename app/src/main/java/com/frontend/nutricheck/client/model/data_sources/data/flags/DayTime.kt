package com.frontend.nutricheck.client.model.data_sources.data.flags

import java.util.Calendar
import java.util.Date

enum class DayTime(private val value: String) {
    BREAKFAST("Breakfast"),
    LUNCH ("Lunch"),
    DINNER ("Dinner"),
    SNACK ("Snack");

    override fun toString(): String {
        return value
    }
    companion object {
    fun dateToDayTime(date: Date): DayTime {
        val calendar = Calendar.getInstance().apply { time = date }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..10 -> BREAKFAST
            in 11..15 -> LUNCH
            in 16..20 -> DINNER
            else -> SNACK
        }
    }
    }
}