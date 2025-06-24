package com.frontend.nutricheck.client.model.persistence.converter

import androidx.room.TypeConverter
import com.frontend.nutricheck.client.model.data_layer.UserData

class UserDataConverter {

    @TypeConverter
    fun toJson(value: UserData?): String = value?.let { JsonUtil.encode(it) } ?: ""

    @TypeConverter
    fun fromJson(raw: String): UserData? = if (raw.isBlank()) null else JsonUtil.decode(raw)
}