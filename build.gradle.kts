// In Jvent/build.gradle.kts

// ADD THIS BUILDSCRIPT BLOCK AT THE TOP OF THE FILE
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.51.1")
    }
}

// REMOVE THE HILT PLUGIN FROM THIS BLOCK
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.android.library") version "8.2.2" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.23" apply false
    // id("com.google.dagger.hilt.android") version "2.51.1" apply false // <-- DELETE OR COMMENT OUT THIS LINE
}