import uk.gov.pipelines.config.ApkConfig
import uk.gov.pipelines.emulator.EmulatorConfig
import uk.gov.pipelines.emulator.SystemImageSource

buildscript {
    dependencies {
        listOf(
            libs.jacoco.agent,
            libs.jacoco.ant,
            libs.jacoco.core,
            libs.jacoco.report,
        ).forEach {
            classpath(it)
        }
    }

    val localProperties = java.util.Properties()
    if (rootProject.file("local.properties").exists()) {
        localProperties.load(java.io.FileInputStream(rootProject.file("local.properties")))
    }

    val githubRepositoryName: String by rootProject.extra("")
    val mavenGroupId: String by rootProject.extra("")

    val buildLogicDir: String by extra("mobile-android-pipelines/buildLogic")
    val sonarProperties: Map<String, String> by extra(
        mapOf(
            "sonar.projectKey" to "di-mobile-android-onelogin-app",
            "sonar.projectId" to "di-mobile-android-onelogin-app",
        )
    )
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.crashlytics) apply false
    alias(libs.plugins.oss.licence.about.libraries) apply false
    id("uk.gov.pipelines.android-root-config")
}

val apkConfig by rootProject.extra(
    object : ApkConfig {
        override val applicationId: String = "uk.gov.onelogin"
        override val debugVersion: String = "DEBUG_VERSION"
        override val sdkVersions = object : ApkConfig.SdkVersions {
            override val minimum = 29
            override val target = 34
            override val compile = 35
        }
    }
)


val emulatorConfig: EmulatorConfig by extra(
    EmulatorConfig(
        systemImageSources = setOf(
            SystemImageSource.AOSP_ATD
        ),
        androidApiLevels = setOf(33),
        deviceFilters = setOf("Pixel XL"),
    )
)

setProperty("configDir", "${rootProject.rootDir}/config")

fun setProperty(
    key: String,
    value: Any,
) {
    rootProject.ext[key] = value
}

val composeVersion by project.extra("1.5.3")
val intentsVersion by project.extra("3.4.0")
val navigationVersion by project.extra("2.6.0")

