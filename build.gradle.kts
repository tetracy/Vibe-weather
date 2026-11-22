// 版本钉于 2026-11 官方最新稳定版。
// AGP 9 起内置 Kotlin（built-in Kotlin），不再单独 apply org.jetbrains.kotlin.android。
buildscript {
    repositories {
        maven("https://maven.aliyun.com/repository/public")
        mavenCentral()
    }
    dependencies {
        // 把内置 Kotlin 的 KGP 升到 2.4.21，与 Compose 编译器插件版本对齐
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.21")
    }
}

plugins {
    id("com.android.application") version "9.5.2" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.21" apply false
}
