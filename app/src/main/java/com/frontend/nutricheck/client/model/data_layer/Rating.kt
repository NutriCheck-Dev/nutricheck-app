package com.frontend.nutricheck.client.model.data_layer

import kotlinx.serialization.Serializable

@Serializable
data class Rating(
    val foodId: String? = "",
    val stars: Int? = 0
)
