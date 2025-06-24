package com.frontend.nutricheck.client.model.persistence.converter

import androidx.room.TypeConverter
import com.frontend.nutricheck.client.model.data_layer.Weight

class WeightConverter {

    @TypeConverter
    fun toJson(value: Weight?): String = value?.let { JsonUtil.encode(it) } ?: ""

    @TypeConverter
    fun fromJson(raw: String): Weight? = if (raw.isBlank()) null else JsonUtil.decode(raw)
}