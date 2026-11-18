plugins {
    id("com.android.application")
}

android {
    namespace = "com.nexusplus.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nexusplus.app"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation("androidx.activity:activity:1.10.1")
    implementation("androidx.media3:media3-exoplayer:1.6.1")
    implementation("androidx.media3:media3-ui:1.6.1")
}
