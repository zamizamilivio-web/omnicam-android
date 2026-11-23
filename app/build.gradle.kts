plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "ai.omnicam.studio"
    compileSdk = 35
    defaultConfig {
        applicationId = "ai.omnicam.studio"
        minSdk = 28
        targetSdk = 35
        versionCode = 3
        versionName = "5.0-alpha.3"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
kotlin {
    jvmToolchain(17)
}
