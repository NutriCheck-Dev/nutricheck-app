package com.frontend.nutricheck.client.model.data_sources.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foods")
data class FoodProduct (
    @PrimaryKey override val id: String = "",
    override val name: String = "",
    override val calories: Double = 0.0,
    override val carbohydrates: Double = 0.0,
    override val protein: Double = 0.0,
    override val fat: Double = 0.0,
    override val servings: Int = 1,
) : FoodComponent
