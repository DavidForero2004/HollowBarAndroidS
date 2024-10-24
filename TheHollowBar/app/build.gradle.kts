plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "1.8.22"
}

android {
    namespace = "com.example.thehollowbar"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.thehollowbar"
        minSdk = 31
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.9.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.airbnb.android:lottie:5.0.3")
    implementation("com.google.android.gms:play-services-maps:19.0.0") // Versión actualizada de Lottie
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
