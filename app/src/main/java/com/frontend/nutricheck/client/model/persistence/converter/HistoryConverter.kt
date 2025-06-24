package com.frontend.nutricheck.client.model.persistence.converter

import androidx.room.TypeConverter
import com.frontend.nutricheck.client.model.data_layer.History

class HistoryConverter {

    @TypeConverter
    fun toJson(value: History?): String = value?.let { JsonUtil.encode(it) } ?: ""

    @TypeConverter
    fun fromJson(raw: String): History? = if (raw.isBlank()) null else JsonUtil.decode(raw)
}
