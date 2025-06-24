package com.frontend.nutricheck.client.model.persistence.converter

import androidx.room.TypeConverter
import com.frontend.nutricheck.client.model.data_layer.Food
import com.frontend.nutricheck.client.model.data_layer.FoodComponent
import com.frontend.nutricheck.client.model.data_layer.Recipe
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic


class FoodComponentConverter {

    private val module = SerializersModule {
        polymorphic(FoodComponent::class) {
            subclass(Food::class, Food.serializer())
            subclass(Recipe::class, Recipe.serializer())
        }
    }

    private val json = Json {
        serializersModule = module
        classDiscriminator = "type"
    }

    @TypeConverter
    fun convertToJson(component: FoodComponent): String =
        json.encodeToString(PolymorphicSerializer(FoodComponent::class), component)

    @TypeConverter
    fun convertFromJson(jsonString: String): FoodComponent =
        json.decodeFromString(PolymorphicSerializer(FoodComponent::class), jsonString)
}