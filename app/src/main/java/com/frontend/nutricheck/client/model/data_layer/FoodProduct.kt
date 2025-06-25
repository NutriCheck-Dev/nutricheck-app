package com.frontend.nutricheck.client.model.data_layer

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable @Entity(tableName = "foods")
data class FoodProduct (
    @PrimaryKey override val id: String = "",
    override val name: String,
    override val calories: Double,
    override val carbohydrates: Double,
    override val protein: Double,
    override val fat: Double,
) : FoodComponent
