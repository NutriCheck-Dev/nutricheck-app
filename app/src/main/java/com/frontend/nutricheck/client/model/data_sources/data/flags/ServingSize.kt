package com.frontend.nutricheck.client.model.data_sources.data.flags

enum class ServingSize(private val amount: Int) {
    ONEGRAM(1),
    TENGRAMS(10),
    ONEHOUNDREDGRAMS(100),
    TWOHOUNDREDGRAMS(200);

    fun getDisplayName(): String {
        return if (this == ONEGRAM) {
            "$amount gram"
        } else {
            "$amount grams"
        }
    }
    fun getAmount(): Int {
        return amount
    }
}