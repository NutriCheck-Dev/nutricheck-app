package com.frontend.nutricheck.client.model.data_layer

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable @Entity(tableName = "histories")
data class History(
    @PrimaryKey val id: String = "",
    @Contextual val date: Date,
    val meals: List<Meal>,
    val carbs: Int = 0,
    val protein: Int = 0,
    val fat: Int = 0,
) {
    private var totalCalories: Int? = 0

    fun updateTotalCalories() {

    }
}
