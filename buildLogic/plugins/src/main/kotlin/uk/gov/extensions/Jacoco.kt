package uk.gov.extensions

import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension

/**
 * https://github.com/cashapp/paparazzi/issues/955
 */
fun Project.excludeAndroidClassesFromJacocoCoverage() {
    tasks.withType<Test>(Test::class.java) {
        extensions.configure<JacocoTaskExtension> {
            excludes = excludes.orEmpty() +
                    listOf(
                        "androidx.core.*",
                        "com.android.*",
                        "android.*",
                    )
        }
    }
}