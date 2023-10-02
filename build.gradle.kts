// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.0.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
}

rootProject.ext {
    this.set("appId", "uk.gov.onelogin")
    this.set("compileSdkVersion", 34)
    this.set("configDir", "${rootProject.rootDir}/config")
    this.set("minSdkVersion", 29)
    this.set("targetSdkVersion", 34)
}

apply(plugin = "lifecycle-base")
apply(from = file(rootProject.ext["configDir"] as String + "/styles/tasks.gradle.kts"))
