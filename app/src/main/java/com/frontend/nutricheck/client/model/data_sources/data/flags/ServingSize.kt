package com.frontend.nutricheck.client.model.data_sources.data.flags

enum class ServingSize(private val amount: Double) {
    ONEGRAM(1.0),
    TENGRAMS(10.0),
    ONEHOUNDREDGRAMS(100.0),
    TWOHOUNDREDGRAMS(200.0);

    fun getDisplayName(): String {
        return if (this == ONEGRAM) {
            "$amount gram"
        } else {
            "$amount grams"
        }
    }
    fun getAmount(): Double {
        return amount
    }
}