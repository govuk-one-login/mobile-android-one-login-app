// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}

rootProject.ext {
    this.set("appId", "uk.gov.onelogin")
    this.set("compileSdkVersion", 33)
    this.set("minSdkVersion", 29)
    this.set("targetSdkVersion", 33)
}
