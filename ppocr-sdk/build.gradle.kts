plugins {
    id("com.android.library")
}

android {
    namespace = "com.whocr.paddle"
    compileSdk = 36

    defaultConfig {
        minSdk = 29
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("com.microsoft.onnxruntime:onnxruntime-android:1.21.1")
    // The legacy 4.5.3 wrapper references removed Android libc symbols on
    // current Sony/Android 15 devices. Use the maintained Android AAR.
    implementation("org.opencv:opencv:4.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
}
