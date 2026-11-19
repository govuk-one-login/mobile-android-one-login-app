plugins {
    id("uk.gov.onelogin.android-lib-config")
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
    id("uk.gov.onelogin.screenshot-test-config")
}

android {
    namespace = "uk.gov.onelogin.libraries.mocknetwork"

    buildFeatures {
        compose = true
    }

    defaultConfig {
        missingDimensionStrategy("env", "build")
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
            it.testLogging {
                events =
                    setOf(
                        org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                        org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
                        org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
                    )
            }
        }
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
    testFixtures {
        enable = true
    }
}

dependencies {
    listOf(
        libs.androidx.core.ktx,
        libs.androidx.appcompat,
        libs.androidx.compose.foundation,
        libs.androidx.compose.material3,
        libs.androidx.compose.ui.tooling.preview,
        libs.androidx.lifecycle.runtime,
        libs.androidx.lifecycle.runtime.compose,
        libs.androidx.hilt.navigation.compose,
        libs.hilt.android,
        libs.componentsv2,
        libs.patterns,
        libs.theme,
        libs.network,
        libs.authentication,
        libs.ktor.client.mock,
        platform(libs.androidx.compose.bom),
    ).forEach(::implementation)

    listOf(
        libs.hilt.android.compiler,
        libs.hilt.compiler,
    ).forEach(::ksp)

    listOf(
        kotlin("test-junit5"),
        libs.mockito.kotlin,
        libs.junit.jupiter,
        libs.junit.jupiter.params,
        libs.junit.vintage.engine,
        platform(libs.junit.bom),
        libs.kotlinx.coroutines.test,
        libs.junit,
        libs.androidx.test.ext.junit,
        libs.androidx.compose.ui.junit4,
        libs.roboelectric,
        testFixtures(projects.core),
    ).forEach(::testImplementation)

    testRuntimeOnly(libs.junit.jupiter.engine)

    // The Compose compiler plugin applies to every source set in this module, so the fixtures
    // need the Compose runtime on their class path even though they contain no Compose code.
    listOf(
        libs.compose.runtime,
        platform(libs.androidx.compose.bom),
    ).forEach(::testFixturesImplementation)

    listOf(
        libs.androidx.espresso.core,
        libs.androidx.test.ext.junit,
        libs.mockito.kotlin,
    ).forEach(::androidTestImplementation)
}
