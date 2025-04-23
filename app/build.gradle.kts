import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
}

val localProperties = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}

android {
    namespace = "com.example.moodtrack"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.moodtrack"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "OPENAI_API_KEY",
            "\"${localProperties.getProperty("OPENAI_API_KEY")}\""
        )

        buildConfigField(
            "String",
            "YOUTUBE_API_KEY",
            "\"${localProperties.getProperty("YOUTUBE_API_KEY")}\""
        )
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
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
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
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

//    implementation("com.google.dagger:hilt-android:2.51.1")
//    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
//
//    implementation(libs.firebase.bom)
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.3")
//    implementation(libs.firebase.auth.ktx)

    // ViewModel + LiveData
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Coroutines untuk Asynchronous
    implementation(libs.kotlinx.coroutines.android)

    // Room Database
    implementation(libs.androidx.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Hilt untuk Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Navigation Component (opsional jika pakai navigation)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.material3)
    implementation(libs.androidx.material.icons.extended)

//    implementation(libs.mpandroidchart)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    //ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    implementation(libs.play.services.auth)

    implementation(libs.ktor.client.serialization)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.coil.compose)

    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")

    implementation(libs.androidx.work.runtime.ktx)

//    implementation("androidx.compose.material:material:1.7.8")
//    implementation("com.google.accompanist:accompanist-swiperefresh:0.28.0")
//    implementation("androidx.compose.material3:material-pullrefresh:1.3.2")
//    implementation("androidx.compose.material3:material3:1.3.2")
    implementation("androidx.compose.material3:material3:1.4.0-alpha12")
//    implementation("androidx.compose.material3:material3-pullrefresh:1.4.0-alpha01")




//    implementation("androidx.compose.material:material-pullrefresh:1.0.0")
}