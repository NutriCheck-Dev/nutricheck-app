package com.frontend.nutricheck.client.model.persistence.converter

import androidx.room.TypeConverter
import com.frontend.nutricheck.client.model.data_layer.Meal

class MealConverter {

    @TypeConverter
    fun toJson(value: Meal?): String = value?.let { JsonUtil.encode(it) } ?: ""

    @TypeConverter
    fun fromJson(raw: String): Meal? = if (raw.isBlank()) null else JsonUtil.decode(raw)
}