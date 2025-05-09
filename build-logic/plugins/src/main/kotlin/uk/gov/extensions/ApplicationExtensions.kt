package uk.gov.extensions

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.AppExtension

object ApplicationExtensions {
    /**
     * Declares the Java Code Coverage (JaCoCo) tool's [version].
     *
     * Also configures the debug build type to enable test coverage for both unit and
     * instrumentation tests.
     */
    fun ApplicationExtension.decorateExtensionWithJacoco(version: String) {
        testCoverage.jacocoVersion = version
        buildTypes {
            this.maybeCreate("debug").apply {
                this.isMinifyEnabled = false
                this.enableAndroidTestCoverage = true
                this.enableUnitTestCoverage = true
            }
        }
    }

    /**
     * Declares the Java Code Coverage (JaCoCo) tool's [version].
     *
     * Also configures the debug build type to enable test coverage for both unit and
     * instrumentation tests.
     */
    fun AppExtension.decorateExtensionWithJacoco(version: String) {
        this.jacoco.jacocoVersion = version
        buildTypes {
            this.maybeCreate("debug").apply {
                this.isMinifyEnabled = false
                this.enableAndroidTestCoverage = true
                this.enableUnitTestCoverage = true
            }
        }
    }
}
