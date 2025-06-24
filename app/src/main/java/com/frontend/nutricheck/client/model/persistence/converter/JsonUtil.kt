package com.frontend.nutricheck.client.model.persistence.converter

import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

object JsonUtil {

    @PublishedApi
    internal val json = Json { encodeDefaults = true }

    inline fun <reified T> encode(value: T): String = json.encodeToString(value)

    inline fun <reified T> decode(jsonString: String): T = json.decodeFromString(jsonString)

}