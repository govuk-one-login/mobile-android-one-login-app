package uk.gov.extensions

import com.android.build.api.dsl.LibraryExtension as DslLibraryExtension
import com.android.build.gradle.LibraryExtension

/**
 * Wrapper object for containing extension functions relating to various implementations of the
 * [LibraryExtension], such as the [DslLibraryExtension] variant.
 */
object LibraryExtensionExt {
    /**
     * Declares the Java Code Coverage (JaCoCo) tool's [version].
     *
     * Also configures the debug build type to enable test coverage for both unit and
     * instrumentation tests.
     */
    fun LibraryExtension.decorateExtensionWithJacoco(version: String) {
        this.jacoco.jacocoVersion = version
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
    fun DslLibraryExtension.decorateExtensionWithJacoco(
        version: String
    ) {
        testCoverage.jacocoVersion = version
        buildTypes {
            this.maybeCreate("debug").apply {
                this.isMinifyEnabled = false
                this.enableAndroidTestCoverage = true
                this.enableUnitTestCoverage = true
            }
        }
    }
}
