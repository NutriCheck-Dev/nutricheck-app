package com.frontend.nutricheck.client.model.data_layer

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.Date

@Entity(tableName = "histories")
data class History(
    @PrimaryKey val id: String = "",
    val date: Date = Date(),
    val meals: List<Meal> = emptyList(),
    val carbohydrates: Int = 0,
    val protein: Int = 0,
    val fat: Int = 0,
) {
    private var totalCalories: Int? = 0

    fun updateTotalCalories() {

    }
}
