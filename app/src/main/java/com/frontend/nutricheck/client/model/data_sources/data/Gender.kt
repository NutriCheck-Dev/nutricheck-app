package com.frontend.nutricheck.client.model.data_sources.data

import android.content.Context
import com.frontend.nutricheck.client.R


enum class Gender(val stringResId: Int) {
    MALE(R.string.userData_label_gender_male),
    FEMALE(R.string.userData_label_gender_female),
    DIVERS((R.string.userData_label_gender_diverse));

    fun getDescription(context : Context): String {
        return context.getString(stringResId)
    }
}