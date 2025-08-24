package com.frontend.nutricheck.client.model.data_sources.data.flags

import android.content.Context
import com.frontend.nutricheck.client.R


enum class Gender(val stringResId: Int) {
    FEMALE(R.string.userData_label_gender_female),
    MALE(R.string.userData_label_gender_male),
    DIVERS((R.string.userData_label_gender_diverse));

    fun getDescription(context : Context): String {
        return context.getString(stringResId)
    }
}