plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.moviemuse"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.moviemuse"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        val propertiesFile = rootProject.file("local.properties")
        val properties = mutableMapOf<String, String>()

        if (propertiesFile.exists()) { //edited code (causing issues from previous code)
            propertiesFile.forEachLine { line ->
                if (line.contains("=")) {
                    val (key, value) = line.split("=", limit = 2)
                    properties[key.trim()] = value.trim()
                }
            }
        }

        val apiKey = properties["API_KEY"] ?: ""
        val tmdbBaseImageUrl = properties["TMDB_BASE_IMAGE_URL"] ?: ""
        val tmdbBaseUrl = properties["TMDB_BASE_URL"] ?: ""


        buildConfigField("String", "TMDB_API_KEY", "\"$apiKey\"")
        buildConfigField("String","TMDB_BASE_IMAGE_URL", "\"$tmdbBaseImageUrl\"")
        buildConfigField("String","TMDB_BASE_URL", "\"$tmdbBaseUrl\"")
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

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    buildFeatures {
        compose = true
        buildConfig = true // do not remove, resolved error with disabled buildconfig
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.coil.compose)

    implementation("androidx.compose.material3:material3:1.2.0") // Material 3
    implementation("androidx.compose.ui:ui:1.5.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")

    implementation ("androidx.navigation:navigation-compose:2.7.2")
    implementation ("androidx.navigation:navigation-compose:2.6.0")


    implementation ("androidx.compose.foundation:foundation:1.5.1")

    implementation ("androidx.biometric:biometric:1.2.0-alpha05")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.firestore.ktx)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    debugImplementation(libs.androidx.ui.test.manifest)
}
