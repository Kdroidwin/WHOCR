import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use(::load)
    }
}
val releaseStoreFile = providers.environmentVariable("WHOCR_RELEASE_STORE_FILE").orNull
    ?: localProperties.getProperty("whocr.release.storeFile")
val releaseStorePassword = providers.environmentVariable("WHOCR_RELEASE_STORE_PASSWORD").orNull
    ?: localProperties.getProperty("whocr.release.storePassword")
val releaseKeyAlias = providers.environmentVariable("WHOCR_RELEASE_KEY_ALIAS").orNull
    ?: localProperties.getProperty("whocr.release.keyAlias")
val releaseKeyPassword = providers.environmentVariable("WHOCR_RELEASE_KEY_PASSWORD").orNull
    ?: localProperties.getProperty("whocr.release.keyPassword")
val hasLocalReleaseSigning = listOf(
    releaseStoreFile,
    releaseStorePassword,
    releaseKeyAlias,
    releaseKeyPassword,
).all { !it.isNullOrBlank() }

android {
    namespace = "com.whocr"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.whocr"
        minSdk = 29
        targetSdk = 36
        versionCode = 2
        versionName = "0.1.1"

        ndk {
            // WHOCR is built and verified for the connected modern Android device.
            // Keeping only arm64 avoids shipping unused x86 and 32-bit native OCR runtimes.
            abiFilters += "arm64-v8a"
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("localRelease") {
            if (hasLocalReleaseSigning) {
                storeFile = rootProject.file(requireNotNull(releaseStoreFile))
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            // Native ONNX/OpenCV bindings use reflection and JNI. Keep the
            // GitHub release build intact unless a full R8 verification is run.
            isMinifyEnabled = false
            if (hasLocalReleaseSigning) {
                signingConfig = signingConfigs.getByName("localRelease")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        debug {
            applicationIdSuffix = ".debug"
        }
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
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
    implementation(project(":ppocr-sdk"))
    implementation(platform("androidx.compose:compose-bom:2026.06.00"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2026.06.00"))

    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.exifinterface:exifinterface:1.4.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    implementation("com.google.mlkit:text-recognition:16.0.1")
    // Both recognizers are packaged in the APK: no model download or network is used.
    implementation("com.google.mlkit:text-recognition-japanese:16.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")

    debugImplementation("androidx.compose.ui:ui-tooling")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation("org.robolectric:robolectric:4.15.1")
}
