plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}


android {
    namespace = "com.grifffith.mindfuljournal"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.grifffith.mindfuljournal"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Navigation Components
    implementation(libs.androidx.navigation.compose)
// AndroidX Libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
// Compose BOM (Bill of Materials) for version alignment
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material.icons.extended.v176)
    implementation (libs.material3)
// Material3
    implementation(libs.androidx.material3)
// Navigation Libraries (runtime and compose)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation (libs.androidx.material3.v101)
    implementation (libs.androidx.material3.vcomposeversion)
    implementation (libs.accompanist.permissions)
    implementation (libs.androidx.material3.v110)
// Latest Compose BOM
    implementation (platform(libs.androidx.compose.bom.v20240100))
    implementation (libs.androidx.compose.material3.material3)
    implementation (libs.androidx.runtime)
    implementation (libs.androidx.animation)
    implementation (libs.androidx.material3.v120beta02)
    implementation (libs.runtime)
    implementation (libs.animation)
    implementation (libs.androidx.animation.core)
    implementation (libs.androidx.animation.v151)
    implementation (libs.ui)
// Jetpack Compose BOM for managing versions
    implementation (platform(libs.androidx.compose.bom.v20240100))
// Compose Material3
    implementation (libs.androidx.compose.material3.material32)
// Compose UI
    implementation (libs.androidx.compose.ui.ui)
    implementation(libs.androidx.material3.v140alpha02)





    implementation (libs.coil.kt.coil.compose) // Use the latest version
    implementation (libs.accompanist.permissions.vlatestversion)


// Compose Runtime
    implementation( libs.androidx.compose.runtime.runtime)
// Compose Animation
    implementation (libs.androidx.compose.animation.animation)
// Compose Tooling for Previews
    implementation (libs.ui.tooling.preview)
    implementation(libs.androidx.ui.test.android)
    implementation(libs.androidx.ui.test.android)
    debugImplementation (libs.ui.tooling)
// Lifecycle support
    implementation (libs.androidx.lifecycle.runtime.compose)
// Activity support for Compose
    implementation (libs.activity.compose)
// Navigation (if required)
    implementation (libs.navigation.compose)
// Testing Dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
// Debugging and Tooling
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)






}


