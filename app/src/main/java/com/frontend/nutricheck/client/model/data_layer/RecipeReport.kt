package com.frontend.nutricheck.client.model.data_layer

import kotlinx.serialization.Serializable

@Serializable
data class RecipeReport(
    val id: String? = "",
    val recipeId: String? = "",
    val comment: String? = "",
)
