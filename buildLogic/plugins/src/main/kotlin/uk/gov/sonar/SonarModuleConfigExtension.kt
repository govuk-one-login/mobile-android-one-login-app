package uk.gov.sonar

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.sonarqube.gradle.SonarExtension

/**
 * Configuration block for the [SonarModuleConfigPlugin].
 *
 * Example usage:
 * ```kotlin
 * // build.gradle[.kts] file:
 * sonarModuleConfig {
 *     this.setSonarEnabled(true) // default value; enables sonar reporting for the gradle module.
 * }
 * ```
 *
 * @param objects The [ObjectFactory], internally obtained via [Project.getObjects].
 */
interface SonarModuleConfigExtension {
    /**
     * Enables sonar reporting for the current [Project]. The inversion of this value becomes the
     * parameter for the [SonarExtension.setSkipProject] function.
     *
     * Defaults to `true` via [Property.convention].
     */
    val enabled: Property<Boolean>

    /**
     * Obtains the currently stored value in the [enabled] [Property].
     *
     * Be mindful when accessing this value as it's subject to change until the
     * [Project's][Project] configuration phase completes, such as within a [Project.afterEvaluate]
     * block.
     */
    fun isSonarEnabled(): Boolean = enabled.get()

    /**
     * Overrides the [Property.convention] value applied to [enabled], ignoring the current
     * [Project] when scanning with the sonar gradle plugin.
     *
     * Once used, this function stops further changes to the [enabled] property.
     */
    fun skipSonarScanning() {
        this.enabled.set(false)
        this.enabled.disallowChanges()
    }

    companion object {

        /**
         * A [Project] extension function that maps the [SonarModuleConfigExtension] to the project
         * for use within the [SonarModuleConfigPlugin].
         */
        internal fun Project.sonarModuleConfig(): SonarModuleConfigExtension = extensions.create(
            "sonarModuleConfig",
            SonarModuleConfigExtension::class.java
        ).apply {
            this.enabled.convention(
                this@sonarModuleConfig.provider { true }
            )
        }
    }
}
