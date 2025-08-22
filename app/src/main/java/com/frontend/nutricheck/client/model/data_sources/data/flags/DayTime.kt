package com.frontend.nutricheck.client.model.data_sources.data.flags

import android.content.Context
import com.frontend.nutricheck.client.R
import java.util.Calendar
import java.util.Date

/**
 * Enum representing different times of the day for meal planning.
 * Each enum value corresponds to a specific meal time and provides a method to get its description.
 */
enum class DayTime(val stringResId: Int) {
    BREAKFAST(R.string.label_breakfast),
    LUNCH (R.string.label_lunch),
    DINNER (R.string.label_dinner),
    SNACK (R.string.label_snack);

    fun getDescription(context: Context): String {
        return context.getString(stringResId)
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