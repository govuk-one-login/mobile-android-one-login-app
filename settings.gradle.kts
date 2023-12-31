pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        // See https://github.com/JLLeitschuh/ktlint-gradle
        id("org.jlleitschuh.gradle.ktlint") version "11.6.1"

        kotlin("jvm") version "1.9.20"
        kotlin("plugin.serialization") version "1.9.21"
        id("com.android.library")
        id("org.jetbrains.kotlin.android")
    }

    includeBuild("buildLogic")
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.github.com/govuk-one-login/mobile-android-ui") {
            if (file("${rootProject.projectDir.path}/github.properties").exists()) {
                val propsFile = File("${rootProject.projectDir.path}/github.properties")
                val props = java.util.Properties().also { it.load(java.io.FileInputStream(propsFile)) }
                val ghUsername = props["ghUsername"] as String?
                val ghToken = props["ghToken"] as String?

                credentials {
                    username = ghUsername
                    password = ghToken
                }
            } else {
                credentials {
                    username = System.getenv("USERNAME")
                    password = System.getenv("TOKEN")
                }
            }
        }
    }
}

plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.60.3"
}

// https://docs.gradle.org/8.0/userguide/kotlin_dsl.html#type-safe-accessors
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "OneLogin-Android"
include(":app")
include(":authentication")

refreshVersions {
    enableBuildSrcLibs()
}
