plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlinx.kover")
    alias(libs.plugins.hilt)
}
kover {
    reports {
        total {
            html {
                onCheck = true
            }
            xml {
                onCheck = true
            }
        }
        filters {
            excludes {
                annotatedBy("javax.annotation.Generated")
                annotatedBy("javax.annotation.processing.Generated")
                annotatedBy("dagger.internal.DaggerGenerated")
                annotatedBy("dagger.Module")
                annotatedBy("dagger.Binds")
                annotatedBy("androidx.compose.runtime.Composable")
                annotatedBy("dagger.hilt.InstallIn")

                classes(
                    // Generated code
                    "*.BuildConfig",
                    "*_Factory*",
                    "*_MembersInjector*",
                    "*Hilt_*",
                    "*.databinding.*",
                    "*ComposableSingletons*",
                    "*hilt_aggregated_deps*",
                    "*HiltModules*",
                    "*_Impl*",

                    //Android Classes
                    "*AndroidCameraController*",
                    "*AndroidImageProcessor*",

                    // Data classes
                    "*exceptions*",

                    // UI Theme & Resources
                    "*.ui.theme.*",
                    "*.ui.view.*",
                    "*.*Theme*",
                    "*.R\$*",

                    // Test classes
                    "*Test*",
                    "*.*Test",
                    "*.test.*"
                )

                packages(
                    "com.frontend.nutricheck.client.ui.theme",
                    "dagger.hilt.internal.aggregatedroot.codegen",
                    "com.frontend.nutricheck.client.ui.view_model.navigation",
                    "com.frontend.nutricheck.client.model.data_sources.persistence.dao",
                    "com.frontend.nutricheck.client.ui.view",
                )
            }
        }
    }
}
android {
    namespace = "com.frontend.nutricheck.client"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.frontend.nutricheck.client"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        ndk {
            // Specifies the ABI configurations of your native
            // libraries Gradle should build and package with your app.
            abiFilters += listOf("x86", "x86_64", "armeabi", "armeabi-v7a",
                "arm64-v8a")
        }

        testInstrumentationRunner = "com.nutricheck.frontend.util.CustomHiltTestRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "false"
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += "version"
    productFlavors {
        create("prod") {
            dimension = "version"
            buildConfigField("String", "BASE_URL","\"https://1d25b66e-2926-4996-bb00-2d7fe74c098f.ka.bw-cloud-instance.org\"")
        }
        create("dev") {
            dimension = "version"
            buildConfigField("String", "BASE_URL","\"http://10.0.2.2:8080\"")
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
        buildConfig = true
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}
configurations.all {
    exclude(group = "com.intellij", module = "annotations")
}

dependencies {

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.androidx.room.runtime.android)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.compose)
    implementation(libs.vico.core)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.exifinterface)
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
    ksp(libs.androidx.room.compiler)
    ksp(libs.hilt.compiler)
    kspAndroidTest(libs.hilt.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.gson)
    implementation(libs.material3)
    implementation(libs.androidx.material3.window.size.class1)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.play.services.base)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.androidx.material.icons.extended.android)
    implementation(libs.hilt.android)
    implementation(libs.moshi.kotlin)
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.compose)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.accompanist.permissions)
    implementation(libs.coil.compose)
    implementation(libs.compose.numberpicker)
    implementation(libs.androidx.exifinterface)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.datastore.preferences)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockwebserver)
    androidTestImplementation(libs.androidx.core)
    androidTestImplementation(libs.androidx.runner)
}