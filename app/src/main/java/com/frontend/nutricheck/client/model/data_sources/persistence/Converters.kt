package com.frontend.nutricheck.client.model.data_sources.persistence

import androidx.room.TypeConverter
import com.frontend.nutricheck.client.model.data_sources.data.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.DayTime
import com.frontend.nutricheck.client.model.data_sources.data.Gender
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Meal
import com.frontend.nutricheck.client.model.data_sources.data.MealItem
import com.frontend.nutricheck.client.model.data_sources.data.RecipeReport
import com.frontend.nutricheck.client.model.data_sources.data.RecipeVisibility
import com.frontend.nutricheck.client.model.data_sources.data.ServingSize
import com.frontend.nutricheck.client.model.data_sources.data.WeightGoal
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.util.Date

class Converters {
    private val moshi = Moshi.Builder().build()

    @TypeConverter
    fun fromDate(date: Date): Long? = date.time

    @TypeConverter
    fun toDate(timestamp: Long?): Date? = timestamp?.let { Date(it) }


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

    @TypeConverter
    fun fromReport(report: RecipeReport?): String =
        moshi
            .adapter(RecipeReport::class.java)
            .toJson(report ?: RecipeReport())

    @TypeConverter
    fun toReport(json: String): RecipeReport? {
        val adapter = moshi.adapter(RecipeReport::class.java)
        return adapter.fromJson(json)
    }
}