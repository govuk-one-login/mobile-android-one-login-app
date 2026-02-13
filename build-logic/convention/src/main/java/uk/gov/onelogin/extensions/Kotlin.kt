package uk.gov.onelogin.extensions

import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

internal fun KotlinAndroidProjectExtension.setJavaVersion() {
    jvmToolchain(JAVA_VERSION)
}

internal fun KotlinJvmProjectExtension.setJavaVersion() {
    jvmToolchain(JAVA_VERSION)
}
