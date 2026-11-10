plugins {
    id("uk.gov.onelogin.android-lib-config")
    alias(libs.plugins.compose.compiler)
    id("uk.gov.onelogin.screenshot-test-config")
}

android {
    namespace = "uk.gov.onelogin.libraries.mockwalletsdk"

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
}

dependencies {
    listOf(
        libs.androidx.core.ktx,
        libs.androidx.compose.foundation,
        libs.androidx.compose.material3,
        libs.androidx.compose.ui.tooling.preview,
        libs.androidx.lifecycle.runtime.compose,
        libs.componentsv2,
        libs.patterns,
        libs.theme,
        platform(libs.androidx.compose.bom),
    ).forEach(::implementation)

    implementation(libs.wallet.sdk)

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

    listOf(
        libs.androidx.espresso.core,
        libs.androidx.test.ext.junit,
        libs.mockito.kotlin,
    ).forEach(::androidTestImplementation)
}
