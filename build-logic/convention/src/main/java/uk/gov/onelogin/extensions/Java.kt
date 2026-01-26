package uk.gov.onelogin.extensions

import org.gradle.api.JavaVersion
import org.gradle.api.plugins.JavaPluginExtension

internal const val JAVA_VERSION = 21

internal fun JavaPluginExtension.setJavaVersion() {
    sourceCompatibility = JavaVersion.toVersion(JAVA_VERSION)
    targetCompatibility = JavaVersion.toVersion(JAVA_VERSION)
}
