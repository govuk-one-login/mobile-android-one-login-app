import java.io.FileInputStream
import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") apply false
    id("org.jetbrains.kotlin.android") apply false
    id("org.jlleitschuh.gradle.ktlint") apply true
    id("com.google.dagger.hilt.android") version "2.50" apply false
}
val localProperties = Properties()
if (rootProject.file("local.properties").exists()) {
    localProperties.load(FileInputStream(rootProject.file("local.properties")))
}

setProperty("appId", "uk.gov.onelogin")
setProperty("compileSdkVersion", 34)
setProperty("configDir", "${rootProject.rootDir}/config")
setProperty("minSdkVersion", 29)
setProperty("targetSdkVersion", 34)
setProperty("versionCode", getVersionCode())
setProperty("versionName", getVersionName())

fun getVersionCode(): Int {
    val code: Int = if (rootProject.hasProperty("versionCode")) {
        rootProject.property("versionCode").toString().toInt()
    } else if (localProperties.getProperty("versionCode") != null) {
        localProperties.getProperty("versionCode").toString().toInt()
    } else {
        throw Error(
            "Version code was not found as a command line parameter or a local property"
        )
    }

    println("VersionCode is set to $code")
    return code
}

fun getVersionName(): String {
    val name: String = if (rootProject.hasProperty("versionName")) {
        rootProject.property("versionName") as String
    } else if (localProperties.getProperty("versionName") != null) {
        localProperties.getProperty("versionName") as String
    } else {
        throw Error(
            "Version name was not found as a command line parameter or a local property"
        )
    }

    println("VersionName is set to $name")
    return name
}

fun setProperty(key: String, value: Any) {
    rootProject.ext[key] = value
}

val composeVersion by project.extra("1.5.3")
val intentsVersion by project.extra("3.4.0")
val navigationVersion by project.extra("2.6.0")

apply(plugin = "lifecycle-base")
apply(from = file(rootProject.ext["configDir"] as String + "/styles/tasks.gradle.kts"))
