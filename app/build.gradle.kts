plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.hanzdev.silangkata"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hanzdev.silangkata"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    // Kunci rilis dibaca dari variabel lingkungan yang diisi workflow GitHub Actions.
    signingConfigs {
        create("release") {
            val keystore = System.getenv("SK_KEYSTORE")
            if (keystore != null) {
                storeFile = file(keystore)
                storePassword = System.getenv("SK_STORE_PASS")
                keyAlias = System.getenv("SK_KEY_ALIAS")
                keyPassword = System.getenv("SK_KEY_PASS")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
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
