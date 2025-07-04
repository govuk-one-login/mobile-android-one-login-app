plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    kotlin("kapt")
    id("uk.gov.onelogin.jvm-toolchains")
    id("uk.gov.jacoco.library-config")
    id("uk.gov.sonar.module-config")
    id("uk.gov.onelogin.emulator-config")
}

apply(from = "${rootProject.extra["configDir"]}/detekt/config.gradle")
apply(from = "${rootProject.extra["configDir"]}/ktlint/config.gradle")

android {
    namespace = "uk.gov.android.onelogin.core"
    compileSdk = rootProject.ext["compileSdkVersion"] as Int

    defaultConfig {
        minSdk = rootProject.ext["minSdkVersion"] as Int
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
    flavorDimensions += "env"
    productFlavors {
        listOf(
            "build",
            "staging",
            "integration",
            "production"
        ).forEach { environment ->
            create(environment) {
                var suffix = ""

                dimension = "env"

                if (environment != "production") {
                    suffix = ".$environment"
                }

                manifestPlaceholders["flavorSuffix"] = suffix
            }
        }
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
            it.testLogging {
                events =
                    setOf(
                        org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                        org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
                        org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
                    )
            }
        }
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }
}

dependencies {
    listOf(
        libs.androidx.compose.ui.junit4,
        libs.androidx.navigation.testing,
        libs.androidx.espresso.intents,
        libs.androidx.espresso.core,
        libs.androidx.test.ext.junit,
        libs.test.core.ktx,
        libs.uiautomator,
        libs.mockito.kotlin
    ).forEach(::androidTestImplementation)

    listOf(
        kotlin("test"),
        libs.hilt.android.testing,
        libs.ktor.client.mock,
        libs.mockito.kotlin,
        libs.junit.jupiter,
        libs.junit.jupiter.params,
        libs.junit.vintage.engine,
        platform(libs.junit.bom),
        libs.kotlinx.coroutines.test,
        libs.classgraph,
        libs.junit,
        libs.roboelectric,
        libs.androidx.compose.ui.junit4,
        libs.androidx.espresso.core,
        libs.androidx.navigation.testing,
        libs.androidx.test.orchestrator,
        libs.logging.test
    ).forEach(::testImplementation)

    testRuntimeOnly(libs.junit.jupiter.engine)

    listOf(
        libs.androidx.test.orchestrator
    ).forEach {
        androidTestUtil(it)
    }

    listOf(
        libs.androidx.compose.ui.tooling,
        libs.androidx.compose.ui.test.manifest
    ).forEach(::debugImplementation)

    listOf(
        libs.androidx.core.ktx,
        platform(libs.androidx.compose.bom),
        libs.material,
        libs.bundles.gov.uk,
        libs.hilt.android,
        libs.androidx.hilt.navigation.compose,
        libs.androidx.compose.foundation,
        libs.androidx.compose.material3,
        libs.androidx.biometric,
        libs.kotlinx.serialization.json,
        libs.ktor.client.android,
        libs.androidx.compose.ui.tooling,
        libs.androidx.compose.ui.tooling.preview
    ).forEach(::implementation)

    listOf(
        libs.hilt.android.compiler,
        libs.hilt.compiler
    ).forEach(::kapt)
}

kapt {
    correctErrorTypes = true
}
