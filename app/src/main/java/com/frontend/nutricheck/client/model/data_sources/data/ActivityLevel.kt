package com.frontend.nutricheck.client.model.data_sources.data

import android.content.Context
import com.frontend.nutricheck.client.R

enum class ActivityLevel(val stringResId: Int) {
    NEVER(R.string.userData_label_activity_level_never),
    OCCASIONALLY(R.string.userData_label_activity_level_occasionally),
    REGULARLY(R.string.userData_label_activity_level_regularly),
    FREQUENTLY(R.string.userData_label_activity_level_frequently),;

    fun getDescription(context: Context): String {
        return context.getString(stringResId)
    }
}