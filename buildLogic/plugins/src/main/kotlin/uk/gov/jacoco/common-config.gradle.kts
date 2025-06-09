package uk.gov.jacoco

import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.jacoco
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import uk.gov.extensions.ProjectExtensions.debugLog
import uk.gov.extensions.ProjectExtensions.libs
import uk.gov.extensions.TestExt.decorateTestTasksWithJacoco

plugins {
    jacoco
}

val depJacoco: String by rootProject.extra(
    project.libs.findVersion("jacoco").get().requiredVersion
)

project.configure<JacocoPluginExtension> {
    this.toolVersion = depJacoco
    project.debugLog("Applied jacoco tool version to jacoco plugin")
}

project.tasks.withType<Test> {
    decorateTestTasksWithJacoco().also {
        project.debugLog("Applied jacoco properties to Test tasks")
    }
}
