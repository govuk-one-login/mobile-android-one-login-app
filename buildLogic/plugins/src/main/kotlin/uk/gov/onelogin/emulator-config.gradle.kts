package uk.gov.onelogin

import com.android.build.gradle.BaseExtension
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.StringReader
import org.gradle.kotlin.dsl.configure
import uk.gov.onelogin.emulator.SystemImageSource
import uk.gov.onelogin.emulator.SystemImageSource.GOOGLE_ATD
import uk.gov.onelogin.emulator.SystemImageSource.GOOGLE_PLAYSTORE
import uk.gov.extensions.BaseExtensions.generateDeviceConfigurations
import uk.gov.extensions.BaseExtensions.generateGetHardwareProfilesTask

plugins {
    id("kotlin-android")
}

private val _hardwareProfileFilter: (String) -> Boolean = {
    it.contains("pixel 5", ignoreCase = true)
}
private val _systemImageSources = listOf(
    GOOGLE_ATD,
    GOOGLE_PLAYSTORE
)

/**
 * Configure both app and library modules via the [BaseExtension].
 *
 * Generates applicable Android Virtual Device (AVD) configurations via
 * [generateGetHardwareProfilesTask] output. These configuration act as Gradle managed devices
 * within a given Gradle module, generating instrumentation test tasks based on the device profiles
 * made.
 */
configure<BaseExtension> {
    /* Extra properties for the plugin. Defers to the root project values. Uses the underscored variants
     * as the initial value if the root project has undefined values.
     */
    val minAndroidVersion: Int by project.extra(29)
    val targetAndroidVersion: Int by project.extra(34)

    /**
     * Android versions to use with the gradle managed devices. Due to how the
     * `createManagedDevice${variant}AndroidTestCoverageReport` task generates within google, it
     * depends on all managed device test tasks, instead of creating a coverage report task per
     * device ID. Therefore, this should be an [IntRange] with a single entry until fixed.
     */
    val managedApiLevels: IntRange by project.extra((30..30))
    val hardwareProfileFilter: (String) -> Boolean by project.extra(_hardwareProfileFilter)
    val systemImageSources: List<SystemImageSource> by project.extra(_systemImageSources)

    val consoleOutputStream = ByteArrayOutputStream()
    val hardwareProfilesTask = generateGetHardwareProfilesTask(project, consoleOutputStream)

    /**
     * Call the hardware profiles task within the gradle configuration stage for the sake of
     * building out the various hardware profiles.
     */
    exec {
        commandLine = hardwareProfilesTask.get().commandLine
        args = hardwareProfilesTask.get().args
        standardOutput = consoleOutputStream
    }

    val hardwareProfileStrings = BufferedReader(
        StringReader(String(consoleOutputStream.toByteArray()))
    )
        .readLines()

    generateDeviceConfigurations(
        apiLevelRange = managedApiLevels,
        hardwareProfileStrings = hardwareProfileStrings.filter(hardwareProfileFilter),
        systemImageSources = systemImageSources
    )
}
