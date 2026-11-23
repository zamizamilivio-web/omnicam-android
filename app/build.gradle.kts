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
        versionCode = 2
        versionName = "5.0-alpha.2"
    }
}
