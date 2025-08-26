package com.frontend.nutricheck.client.model.data_sources.data.flags

/**
 * Enum representing the visibility of a recipe.
 */
enum class RecipeVisibility {
    PUBLIC,
    OWNER;

    override fun toString(): String {
        return when (this) {
            PUBLIC -> "public"
            OWNER -> "owner"
        }
    }
}