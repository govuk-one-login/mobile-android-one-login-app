
import java.io.FileInputStream
import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") apply false
    id("org.jetbrains.kotlin.android") apply false
    id("org.jlleitschuh.gradle.ktlint") apply true
}

rootProject.ext {
    this.set("appId", "uk.gov.onelogin")
    this.set("compileSdkVersion", 34)
    this.set("configDir", "${rootProject.rootDir}/config")
    this.set("minSdkVersion", 29)
    this.set("targetSdkVersion", 34)

    val localProperties = Properties()
    if (rootProject.file("local.properties").exists()) {
        localProperties.load(FileInputStream(rootProject.file("local.properties")))
    }

    fun getVersionCode(): Int {
        var code: Int

        if (rootProject.hasProperty("versionCode")) {
            code = rootProject.property("versionCode").toString().toInt()
        } else if (localProperties.getProperty("versionCode") != null) {
            code = localProperties.getProperty("versionCode").toString().toInt()
        } else {
            throw Error(
                "Version code was not found as a command line parameter or a local property"
            )
        }

        println("VersionCode is set to $code")
        return code
    }

    fun getVersionName(): String {
        var name: String

        if (rootProject.hasProperty("versionName")) {
            name = rootProject.property("versionName") as String
        } else if (localProperties.getProperty("versionName") != null) {
            name = localProperties.getProperty("versionName") as String
        } else {
            throw Error(
                "Version name was not found as a command line parameter or a local property"
            )
        }

        println("VersionName is set to $name")
        return name
    }

    this.set("versionCode", getVersionCode())
    this.set("versionName", getVersionName())
}

val composeVersion by project.extra("1.5.3")
val intentsVersion by project.extra("3.4.0")
val navigationVersion by project.extra("2.6.0")

apply(plugin = "lifecycle-base")
apply(from = file(rootProject.ext["configDir"] as String + "/styles/tasks.gradle.kts"))
