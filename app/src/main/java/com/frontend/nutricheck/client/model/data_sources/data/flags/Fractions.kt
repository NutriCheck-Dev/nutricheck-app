package com.frontend.nutricheck.client.model.data_sources.data.flags

enum class Fractions(
    private val displayName: String,
    private val value: Double
    ) {
    ONE_EIGHTH("1/8", 0.125),
    QUARTER("1/4", 0.25),
    ONE_THIRD("1/3", 0.333),
    THREE_EIGHTHS("3/8", 0.375),
    ONE_HALF("1/2", 0.5),
    FIVE_EIGHTHS("5/8", 0.625),
    TWO_THIRDS("2/3", 0.666),
    THREE_QUARTERS("3/4", 0.75),
    SEVEN_EIGHTHS("7/8", 0.875);

    override fun toString(): String {
        return displayName
    }

    fun toDouble(): Double {
        return value
    }
}