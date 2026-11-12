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

        // Transitive dependencies of the Gradle plugin classpath (AGP, Kotlin Gradle plugin,
        // the shared pipelines plugins) flagged by Dependabot against `settings.gradle.kts`.
        // These live only on the build/plugin classpath, never in the shipped app, but are
        // constrained here to patched versions to clear the alerts.
        constraints {
            classpath(libs.patch.guava) {
                because("Earlier versions contain known vulnerabilities (GHSA-xxph-c9ww-hj94)")
            }
            // The bouncycastle modules must move together — mixing bcprov 1.85 with older
            // bcutil/bcpkix causes a NoSuchFieldError at runtime.
            classpath(libs.bundles.tooling.security.bouncycastle) {
                because("Earlier versions contain known vulnerabilities (GHSA-9pwp-9qqc-pr26, GHSA-qp49-qgx5-5m26)")
            }
            classpath(libs.patch.logback.core) {
                because("Earlier versions contain known vulnerabilities (GHSA-jhq6-gfmj-v8fx)")
            }
        }
    }

    val localProperties = java.util.Properties()
    if (rootProject.file("local.properties").exists()) {
        localProperties.load(java.io.FileInputStream(rootProject.file("local.properties")))
    }

    val getVersionCode: () -> Int = {
        val code: Int =
            if (rootProject.hasProperty("versionCode")) {
                rootProject.property("versionCode").toString().toInt()
            } else if (localProperties.getProperty("versionCode") != null) {
                localProperties.getProperty("versionCode").toString().toInt()
            } else {
                throw Error(
                    "Version code was not found as a command line parameter or a local property",
                )
            }

        println("VersionCode is set to $code")
        code
    }

    val getVersionName: () -> String = {
        val name: String =
            if (rootProject.hasProperty("versionName")) {
                rootProject.property("versionName") as String
            } else if (localProperties.getProperty("versionName") != null) {
                localProperties.getProperty("versionName") as String
            } else {
                throw Error(
                    "Version name was not found as a command line parameter or a local property",
                )
            }

        println("VersionName is set to $name")
        name
    }

    val versionCode: Int by rootProject.extra(
        getVersionCode()
    )
    val versionName: String by rootProject.extra(
        getVersionName()
    )

    val debugBuildAppCheckToken: String by rootProject.extra(
        try {
            providers.gradleProperty("debugBuildAppCheckToken").get()
        }  catch (e: org.gradle.api.internal.provider.MissingValueException) {
            logger.warn("firebase debug build token not found in gradle properties")
            System.getenv("BUILD_DEBUG_APP_CHECK_TOKEN") ?: ""
        }
    )
    val debugStagingAppCheckToken: String by rootProject.extra(
        try {
            providers.gradleProperty("debugStagingAppCheckToken").get()
        }  catch (e: org.gradle.api.internal.provider.MissingValueException) {
            logger.warn("firebase debug staging token not found in gradle properties")
            ""
        }
    )
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
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.crashlytics) apply false
    alias(libs.plugins.oss.licence.about.libraries) apply false
    alias(libs.plugins.paparazzi) apply false
    id("uk.gov.pipelines.android-root-config")
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.ksp) apply false
}

val apkConfig by rootProject.extra(
    object : ApkConfig {
        override val applicationId: String = "uk.gov.onelogin"
        override val debugVersion: String = "DEBUG_VERSION"
        override val sdkVersions = object : ApkConfig.SdkVersions {
            override val minimum = 29
            override val target = 36
            override val compile = 37
        }
    }
)
val emulatorConfig: EmulatorConfig by extra(
    EmulatorConfig(
        systemImageSources = setOf(
            SystemImageSource.AOSP_ATD
        ),
        androidApiLevels = setOf(36),
        deviceFilters = setOf("Pixel XL"),
    )
)

val jacocoVersion: String by rootProject.extra(
    libs.versions.jacoco.get(),
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
