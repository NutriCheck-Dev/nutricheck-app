package com.frontend.nutricheck.client.model.data_sources.persistence.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.frontend.nutricheck.client.model.data_sources.data.ServingSize

@Entity(tableName = "foods")
data class FoodProductEntity (
    @PrimaryKey val id: String,
    val name: String,
    val calories: Double,
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double,
    val servings: Double,
    val servingSize: ServingSize
)