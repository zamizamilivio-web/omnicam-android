plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "ai.omnicam.studio"
    compileSdk = 35
    defaultConfig {
        applicationId = "ai.omnicam.studio.test"
        minSdk = 28
        targetSdk = 35
        versionCode = 4
        versionName = "5.0-test.1"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
kotlin {
    jvmToolchain(17)
}
