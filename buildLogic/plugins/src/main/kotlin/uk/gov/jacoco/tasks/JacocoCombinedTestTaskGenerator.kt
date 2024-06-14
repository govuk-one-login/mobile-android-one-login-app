package uk.gov.jacoco.tasks

import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import uk.gov.onelogin.Filters
import uk.gov.onelogin.filetree.fetcher.FileTreeFetcher
import uk.gov.jacoco.config.JacocoCombinedTestConfig
import uk.gov.jacoco.config.JacocoCustomConfig

/**
 * A [JacocoTaskGenerator] implementation for combining the provided [JacocoTaskGenerator]
 * [configurations].
 *
 * @param project The Gradle [Project] that houses the generated Jacoco task. Used to generate the
 * relevant [JacocoCustomConfig] instance and the default value for [reportDirectoryPrefix].
 * @param classDirectoriesFetcher The [FileTreeFetcher] that provides the class directories used for
 * reporting code coverage through Jacoco.
 * @param variant The name of the build variant. Used as a parameter to obtain relevant Gradle
 * tasks.
 * @param reportDirectoryPrefix The absolute path that acts as a starting location for the
 * [project]'s custom Jacoco reports.
 * @param configurations The [JacocoTaskGenerator] instances to aggregate. Used to access the
 * relevant [JacocoCustomConfig] tasks that in turn get generated.
 */
class JacocoCombinedTestTaskGenerator(
    private val project: Project,
    private val classDirectoriesFetcher: FileTreeFetcher,
    variant: String,
    private val reportDirectoryPrefix: String = "${project.buildDir}/reports/jacoco",
    private val configurations: Iterable<JacocoCustomConfig>
) : JacocoTaskGenerator {

    private val capitalisedVariantName = variant.capitalized()

    private val name: String = "jacoco${capitalisedVariantName}CombinedTestReport"

    private val configuration = JacocoCombinedTestConfig(
        project,
        classDirectoriesFetcher,
        name = name,
        configurations
    )

    override fun generate() {
        configuration.generateCustomJacocoReport(
            excludes = Filters.androidUnitTests,
            dependencies = configurations.map { it.testTaskName },
            description = "Create coverage report from the '$capitalisedVariantName' test reports.",
            reportOutputDir = "$reportDirectoryPrefix/combined"
        )
    }
}
