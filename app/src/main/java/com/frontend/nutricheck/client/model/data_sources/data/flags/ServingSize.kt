package com.frontend.nutricheck.client.model.data_sources.data.flags

import android.content.Context
import com.frontend.nutricheck.client.R

/**
 * Enum representing the serving sizes for food products.
 * Each enum constant corresponds to a specific serving size in grams.
 */
enum class ServingSize(private val amount: Double) {
    ONEGRAM(1.0),
    TENGRAMS(10.0),
    ONEHOUNDREDGRAMS(100.0),
    TWOHOUNDREDGRAMS(200.0);

    fun getDisplayName(context: Context): String {
        return if (this == ONEGRAM) {
            context.getString(R.string.serving_size_gram, amount.toInt())
        } else {
            context.getString(R.string.serving_size_grams, amount.toInt())
        }
    }

    fun getAmount(): Double = amount
}