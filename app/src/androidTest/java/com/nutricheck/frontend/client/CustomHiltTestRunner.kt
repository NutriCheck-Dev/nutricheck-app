package com.nutricheck.frontend.client

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class CustomHiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader,
        className: String,
        context: Context
    ): Application {
        return super.newApplication(
            cl,
            "com.google.dagger.hilt.android.testing.HiltTestApplication",
            context
        )
    }
}