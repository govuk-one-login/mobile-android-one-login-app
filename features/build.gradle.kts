

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.oss.licence.about.libraries)
    alias(libs.plugins.paparazzi)
    kotlin("kapt")
    id("uk.gov.onelogin.jvm-toolchains")
    id("uk.gov.jacoco.library-config")
    id("uk.gov.sonar.module-config")
    id("uk.gov.onelogin.emulator-config")
}

apply(from = "${rootProject.extra["configDir"]}/detekt/config.gradle")
apply(from = "${rootProject.extra["configDir"]}/ktlint/config.gradle")

android {
    namespace = "uk.gov.android.onelogin.features"
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
                if (environment == "integration") {
                    matchingFallbacks.add("production")
                }
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
    packaging {
        resources.excludes += setOf(
            "META-INF/LICENSE-notice.md",
            "META-INF/versions/9/OSGI-INF/MANIFEST.MF",
            "META-INF/LICENSE.md"
        )
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
        libs.mockito.kotlin,
        libs.mockito.android,
        libs.logging.test
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
        libs.androidx.espresso.intents,
        libs.logging.test,
        testFixtures(projects.core)
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
        libs.androidx.appcompat,
        platform(libs.androidx.compose.bom),
        libs.material,
        libs.androidx.compose.material,
        libs.androidx.compose.material3,
        libs.bundles.gov.uk,
        libs.androidx.hilt.navigation.compose,
        projects.core,
        projects.featureflags,
        libs.kotlinx.serialization.json,
        libs.androidx.compose.foundation,
        platform(libs.firebase.bom),
        libs.bundles.firebase,
        libs.ktor.client.android,
        libs.androidx.constraintlayout,
        libs.hilt.android,
        libs.androidx.compose.ui.tooling,
        libs.androidx.compose.ui.tooling.preview,
        libs.bundles.about.libraries
    ).forEach(::implementation)

    implementation(libs.wallet.sdk) {
        exclude(group = "uk.gov.android", module = "network")
        exclude(group = "uk.gov.securestore", module = "app")
    }

    listOf(
        libs.hilt.android.compiler,
        libs.hilt.compiler
    ).forEach(::kapt)
}

kapt {
    correctErrorTypes = true
}
