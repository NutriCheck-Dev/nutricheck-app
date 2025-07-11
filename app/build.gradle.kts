plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.nutricheck.frontend"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nutricheck.frontend"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api"

    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.androidx.room.runtime.android)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.androidx.hilt.navigation.compose)
    implementation(libs.jakarta.inject.api)
    implementation("com.patrykandpatrick.vico:compose:1.13.0")
    implementation("com.patrykandpatrick.vico:core:1.13.0")
    implementation(libs.androidx.room.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.gson)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.material3)
    implementation(libs.androidx.material3.window.size.class1)
    implementation("androidx.compose.material:material-icons-extended:<1.5.0>")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.8.3")
    implementation("com.google.android.gms:play-services-base:18.7.1")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

}