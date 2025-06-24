package com.frontend.nutricheck.client.model.data_layer

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable @Entity(tableName = "ingredients")
data class Ingredients (
    @PrimaryKey override val id: String = "",
    override val name: String,
    override val calories: Int,
    override val carbohydrates: Int,
    override val protein: Int,
    override val fat: Int
) : FoodComponent
