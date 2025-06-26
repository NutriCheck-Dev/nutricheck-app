package com.frontend.nutricheck.client.model.data_layer

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.collections.plus

@Entity(tableName = "meals")
data class Meal (
    @PrimaryKey val id: String = "",
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