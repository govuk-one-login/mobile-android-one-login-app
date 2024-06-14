package uk.gov.sonar

import java.io.File
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.sonarqube.gradle.SonarExtension
import uk.gov.onelogin.Filters
import uk.gov.onelogin.SourceSetFolder
import uk.gov.sonar.SonarModuleConfigExtension.Companion.sonarModuleConfig

/**
 * Custom convention plugin that configures the [SonarExtension] when applied.
 *
 * Configure this via the [SonarModuleConfigExtension].
 */
@Suppress("TooManyFunctions")
class SonarModuleConfigPlugin : Plugin<Project> {

    override fun apply(target: Project) = target.run {
        val extensionConfiguration = sonarModuleConfig()
        plugins.apply("org.sonarqube")

        // provide the sonar properties to the given project
        configure<SonarExtension> {
            properties {
                properties(projectSonarProperties(target))
            }
        }

        // declare whether sonar should report for this project
        project.afterEvaluate {
            configure<SonarExtension> {
                isSkipProject = !extensionConfiguration.isSonarEnabled()
            }
        }
    }

    private fun generateCommaSeparatedFiles(
        project: Project,
        iterator: Iterable<String>
    ) = project.fileTree(project.projectDir) {
        this.setIncludes(iterator)
    }.files.joinToString(
        separator = ",",
        transform = File::getAbsolutePath
    )

    private fun androidLintReportFiles(project: Project) =
        generateCommaSeparatedFiles(project, listOf("**/reports/lint-results-*.xml"))

    private fun detektReportFiles(project: Project) =
        generateCommaSeparatedFiles(project, listOf("**/reports/detekt/*.xml"))

    private fun jacocoXmlReportFiles(project: Project) = generateCommaSeparatedFiles(
        project,
        listOf(
            "**/reports/coverage/**/*.xml", // android instrumentation test reports
            "**/reports/jacoco/**/*.xml" // unit test reports
        )
    )

    private fun junitReportFiles(project: Project): String = generateCommaSeparatedFiles(
        project,
        listOf(
            "**/outputs/androidTest-results/managedDevice/flavors/*/", // instrumentation
            "**/test-results" // unit tests
        )
    )

    private fun ktLintReportFiles(project: Project) =
        generateCommaSeparatedFiles(project, listOf("**/reports/ktlint/**/*.xml"))

    private fun sonarExclusions() =
        listOf(
            Filters.androidInstrumentationTests,
            Filters.sonar,
            Filters.testSourceSets
        ).flatten().joinToString(separator = ",")

    private fun sourceFolders(
        moduleSourceFolder: SourceSetFolder
    ) = moduleSourceFolder.commaSeparatedSourceFolders

    private fun testFolders(
        moduleSourceFolder: SourceSetFolder
    ) = moduleSourceFolder.testFolders

    private fun projectSonarProperties(project: Project): MutableMap<String, Any> = mutableMapOf(
        "sonar.sources" to sourceFolders(SourceSetFolder(project)),
        "sonar.tests" to testFolders(SourceSetFolder(project)),
        "sonar.exclusions" to sonarExclusions(),
        "sonar.androidLint.reportPaths" to androidLintReportFiles(project),
        "sonar.coverage.jacoco.xmlReportPaths" to jacocoXmlReportFiles(project),
        "sonar.kotlin.detekt.reportPaths" to detektReportFiles(project),
        "sonar.kotlin.ktlint.reportPaths" to ktLintReportFiles(project),
        "sonar.junit.reportPaths" to junitReportFiles(project),
    )
}