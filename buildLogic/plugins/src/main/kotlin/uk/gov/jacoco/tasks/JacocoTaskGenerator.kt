package uk.gov.jacoco.tasks

import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * Abstraction that acts as the entry point for generating a customised [JacocoReport] Gradle task.
 *
 * @property name The name of the generated [JacocoReport] Gradle task.
 * @property configuration The properties for generating a [JacocoReport] Gradle task.
 */
fun interface JacocoTaskGenerator {

    /**
     * Registers a [JacocoReport] Gradle task to a given project.
     */
    fun generate()
}
