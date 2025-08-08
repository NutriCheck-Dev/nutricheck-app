package com.frontend.nutricheck.client.model.data_sources.persistence

import androidx.room.TypeConverter
import com.frontend.nutricheck.client.model.data_sources.data.flags.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.model.data_sources.data.flags.Gender
import com.frontend.nutricheck.client.model.data_sources.data.flags.RecipeVisibility
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize
import com.frontend.nutricheck.client.model.data_sources.data.flags.WeightGoal
import com.squareup.moshi.Moshi
import java.util.Calendar
import java.util.Date

class Converters {
    private val moshi = Moshi.Builder().build()

    @TypeConverter
    fun fromDate(date: Date): Long? {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        if (timestamp == null) return null
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.time
    }


    @TypeConverter
    fun fromGender(gender: Gender?): String? = gender?.name

    @TypeConverter
    fun toGender(name: String?): Gender? = name?.let { Gender.valueOf(it) }

    @TypeConverter
    fun fromActivityLevel(level: ActivityLevel?): String? = level?.name

    @TypeConverter
    fun toActivityLevel(name: String?): ActivityLevel? = name?.let { ActivityLevel.valueOf(it) }

    @TypeConverter
    fun fromWeightGoal(goal: WeightGoal?): String? = goal?.name

    @TypeConverter
    fun toWeightGoal(name: String?): WeightGoal? = name?.let { WeightGoal.valueOf(it) }

    @TypeConverter
    fun fromRecipeVisibility(visibility: RecipeVisibility?): String? = visibility?.name

    @TypeConverter
    fun toRecipeVisibility(name: String?): RecipeVisibility? = name?.let { RecipeVisibility.valueOf(it) }

    @TypeConverter
    fun fromServingSize(servingSize: ServingSize?): Int? = servingSize?.ordinal

    @TypeConverter
    fun toServingSize(ordinal: Int?): ServingSize? = ordinal?.let { ServingSize.entries.getOrNull(it) }

    @TypeConverter
    fun fromDayTime(dayTime: DayTime?): String? = dayTime?.name

    @TypeConverter
    fun toDayTime(name: String?): DayTime? = name?.let { DayTime.valueOf(it) }

}