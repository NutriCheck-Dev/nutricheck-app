package com.frontend.nutricheck.client.model.data_layer

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable @Entity(tableName = "foods")
data class Food (
    @PrimaryKey override val id: String = "",
    override val name: String = "",
    override val calories: Int = 0,
    override val protein: Int = 0,
    override val carbs: Int = 0,
    override val fat: Int = 0
) : FoodComponent
