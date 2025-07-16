package com.frontend.nutricheck.client.model.data_sources.data

import kotlin.collections.plus

data class Meal (
    val historyDayId: String = "",
    val dayTime: DayTime = DayTime.BREAKFAST,
    val items: Set<MealItem> = emptySet()
) {
    fun addItem(item: MealItem): Meal {
        val updated = this.copy(items = this.items + item)
        return updated
    }
    fun removeItem(item: MealItem): Meal {
        val updated = this.copy(items = this.items.filterNot { it == item }.toSet())
        return updated
    }
}