package com.frontend.nutricheck.client.model.data_layer

import kotlinx.serialization.Serializable


data class RecipeReport(
    val id: String? = "",
    val description: String? = "",
    val recipeId: String? = "",
    val recipeName: String? = "",
    val recipeInstructions: String? = ""
)
