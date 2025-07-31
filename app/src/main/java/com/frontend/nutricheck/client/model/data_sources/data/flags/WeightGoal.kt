package com.frontend.nutricheck.client.model.data_sources.data.flags

import android.content.Context
import com.frontend.nutricheck.client.R
/**
 * Enum class representing the user's weight goal.
 * Each value corresponds to a specific goal and holds a reference to a string resource ID for description.
 *
 * @property resId Resource ID for the goal description.
 */
enum class WeightGoal(val resId: Int) {
    LOSE_WEIGHT(R.string.userData_label_goal_lose_weight),
    MAINTAIN_WEIGHT(R.string.userData_label_goal_maintain_weight),
    GAIN_WEIGHT(R.string.userData_label_goal_gain_weight);
    /**
     * Returns the description of the weight goal as a string.
     *
     * @param context Context for accessing resources.
     * @return Description of the weight goal.
     */
    fun getDescription(context : Context): String {
        return context.getString(resId)
    }
}