package uk.gov.onelogin.extensions

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion

/**
 * Type alias for configuring both Android application and Android library modules.
 */
private typealias AndroidExtension = CommonExtension<*, *, *, *, *, *>

internal fun AndroidExtension.setJavaVersion() =
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(JAVA_VERSION)
        targetCompatibility = JavaVersion.toVersion(JAVA_VERSION)
    }
