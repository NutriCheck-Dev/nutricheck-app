package com.frontend.nutricheck.client.model.data_sources.data

import android.content.Context
import com.frontend.nutricheck.client.R

enum class WeightGoal(val resId: Int) {
    LOSE_WEIGHT(R.string.userData_label_goal_lose_weight),
    MAINTAIN_WEIGHT(R.string.userData_label_goal_maintain_weight),
    GAIN_WEIGHT(R.string.userData_label_goal_gain_weight);

    fun getDescription(context : Context): String {
        return context.getString(resId)
    }
}