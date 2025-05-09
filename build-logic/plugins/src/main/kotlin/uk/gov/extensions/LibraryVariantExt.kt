package uk.gov.extensions

import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.LibraryVariant
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import uk.gov.onelogin.filetree.fetcher.AsmFileTreeFetcher
import uk.gov.onelogin.filetree.fetcher.FileTreesFetcher
import uk.gov.onelogin.filetree.fetcher.JavaCompileFileTreeFetcher
import uk.gov.jacoco.tasks.JacocoManagedDeviceTaskGenerator
import uk.gov.jacoco.tasks.JacocoTaskGenerator
import uk.gov.jacoco.tasks.JacocoUnitTestTaskGenerator

fun LibraryVariant.generateDebugJacocoTasks(
    project: Project
) {
    val capitalisedFlavorName = flavorName?.capitalized() ?: error(
        "The library variant has no flavor name!"
    )
    generateJacocoTasks(
        project,
        buildType.name,
        name,
        capitalisedFlavorName
    )
}

fun ApplicationVariant.generateDebugJacocoTasks(
    project: Project
) {
    val capitalisedFlavorName = flavorName?.capitalized() ?: error(
        "The application variant has no flavor name!"
    )
    generateJacocoTasks(
        project,
        buildType.name,
        name,
        capitalisedFlavorName
    )
}

/**
 * Creates then invokes different kinds of [JacocoTaskGenerator].
 *
 * @see JacocoConnectedTestTaskGenerator
 * @see JacocoManagedDeviceTaskGenerator
 * @see JacocoUnitTestTaskGenerator
 */
private fun generateJacocoTasks(
    project: Project,
    buildType: String?,
    name: String,
    capitalisedFlavorName: String
) {
    if (buildType == "debug") {
        val classDirectoriesFetcher = generateClassDirectoriesFetcher(
            project,
            name,
            capitalisedFlavorName
        )

        val unitTestReportGenerator = JacocoUnitTestTaskGenerator(
            project,
            classDirectoriesFetcher,
            name
        )

        val managedDeviceTestReportGenerator = JacocoManagedDeviceTaskGenerator(
            project,
            classDirectoriesFetcher
        )

        listOf(
            unitTestReportGenerator,
            managedDeviceTestReportGenerator
        ).forEach(JacocoTaskGenerator::generate)
    }
}

/**
 * Provides a [FileTreesFetcher] that obtains the output directories of compilation tasks.
 *
 * The fetcher then provides the locations as part of configuring JaCoCo.
 */
private fun generateClassDirectoriesFetcher(
    project: Project,
    name: String,
    capitalisedFlavorName: String
): FileTreesFetcher = FileTreesFetcher(
    project,
    AsmFileTreeFetcher(
        project,
        name,
        capitalisedFlavorName
    ),
    JavaCompileFileTreeFetcher(
        project,
        name,
        capitalisedFlavorName
    )
)
