plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.vibe.weather"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.vibe.weather"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0-m0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        // 内置 Kotlin 的 jvmTarget 默认取 targetCompatibility，无需另设
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // 版本组合钉在 API 36 时代：本机只装了平台 35/36，且 dl.google.com 被墙暂时装不了 37。
    // 2026.11 的最新 BOM (Compose 1.13+) 要求 compileSdk 37+。装上平台 37 后可整体升级。
    implementation(platform("androidx.compose:compose-bom:2025.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation("androidx.core:core-ktx:1.17.0")

    // Haze 2.x 全线要求 compileSdk 37（2026-11-22 实测 AAR 元数据 minCompileSdk=37），
    // 本机装不了平台 37，玻璃卡暂以同规格纱面实现；平台 37 可装后接回（docs/research §2.3）。

    testImplementation("junit:junit:4.13.2")
}
