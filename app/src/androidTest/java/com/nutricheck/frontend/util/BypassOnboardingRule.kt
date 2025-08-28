package com.nutricheck.frontend.util

import android.app.Application
import com.frontend.nutricheck.client.model.data_sources.data.flags.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.flags.Gender
import com.frontend.nutricheck.client.model.data_sources.data.flags.WeightGoal
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Weight
import com.frontend.nutricheck.client.ui.view_model.utils.UserDataUtilsLogic
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.runBlocking
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.Date
import java.util.GregorianCalendar

class BypassOnboardingRule(
    private val application: Application
) : TestRule {

    override fun apply(base: Statement, description: Description) = object : Statement() {
        override fun evaluate() {
            val entry = EntryPointAccessors.fromApplication(application, RepoEntryPoint::class.java)

            runBlocking {
                val now = Date()
                val user = UserData(
                    username = "Max",
                    birthdate = GregorianCalendar(1990, 1, 1).time,
                    gender = Gender.MALE,
                    height = 180.0,
                    weight = 80.0,
                    activityLevel = ActivityLevel.FREQUENTLY,
                    weightGoal = WeightGoal.MAINTAIN_WEIGHT,
                    targetWeight = 81.0,
                    age = 24,
                    dailyCaloriesGoal = 0, proteinGoal = 0, carbsGoal = 0, fatsGoal = 0
                )
                val userWithCalc = UserDataUtilsLogic.calculateNutrition(user)
                entry.appSettingsRepository().setOnboardingCompleted()
                entry.userDataRepository()
                    .addUserDataAndAddWeight(userWithCalc, Weight(user.weight, now))
            }

            base.evaluate()
        }
    }
}