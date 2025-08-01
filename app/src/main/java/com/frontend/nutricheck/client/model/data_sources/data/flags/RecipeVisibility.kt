package com.frontend.nutricheck.client.model.data_sources.data.flags

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