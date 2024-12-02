plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("androidx.navigation.safeargs.kotlin")
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.carpoolapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.carpoolapp"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    kapt {
        correctErrorTypes = true
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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.firestore.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Fragment
    implementation(libs.androidx.fragment.ktx) // Or the latest version

    //Google Authentication
//    implementation(libs.google.play.services.auth)
    implementation(libs.play.services.auth.v2070)

    //ViewModel and Coroutines for MVI
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.jetbrains.kotlinx.coroutines.play.services)

    //Hilt Dependency Injection
    implementation(libs.hilt.android.v2511) // Add this line
    kapt(libs.hilt.compiler.v2511)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    kapt(libs.androidx.hilt.compiler.v100)

    implementation(libs.sdp.android)
    implementation(libs.ssp.android)

    implementation(libs.firebase.database)

    implementation(platform(libs.firebase.bom))

    //Firebase Authentication
    implementation(libs.firebase.auth)

}