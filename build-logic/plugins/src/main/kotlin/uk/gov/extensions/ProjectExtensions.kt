package uk.gov.extensions

import gradle.kotlin.dsl.accessors._de1ae95fd48a1440f45f50d171d58be3.ext
import java.io.ByteArrayOutputStream
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.gradle.process.ExecSpec
import uk.gov.output.OutputStreamGroup

object ProjectExtensions {
    /**
     * Performs a terminal command provided within the [spec] parameter. Stores the output from
     * the command into a temporary [ByteArrayOutputStream].
     *
     * Use this function when there's a need to defer to a terminal command for the correct value.
     *
     * @return The string output sent to the temporary [ByteArrayOutputStream].
     * @sample Project.versionName
     */
    fun Project.execWithOutput(spec: ExecSpec.() -> Unit) =
        OutputStreamGroup().use { outputStreamGroup ->
            val byteArrayOutputStream = ByteArrayOutputStream()
            outputStreamGroup.add(byteArrayOutputStream)
            exec {
                this.spec()
                outputStreamGroup.add(this.standardOutput)
                this.standardOutput = outputStreamGroup
            }
            byteArrayOutputStream.toString().trim()
        }

    /**
     * Obtains the `
     */
    val Project.versionCode
        get() = rootProject.ext["versionCode"] as String
    val Project.versionName
        get() = rootProject.ext["versionName"] as String

    /**
     * Obtains a `-Dkey` property from the provided terminal command.
     */
    private fun Project.prop(key: String, default: Any): String {
        return providers.gradleProperty(key).getOrElse(default.toString())
    }

    fun Project.debugLog(messageSuffix: String) {
        logger.debug("${project.path}: $messageSuffix")
    }

    val Project.libs
        get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
}
