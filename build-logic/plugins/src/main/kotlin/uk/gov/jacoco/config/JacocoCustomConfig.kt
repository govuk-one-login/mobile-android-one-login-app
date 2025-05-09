package uk.gov.jacoco.config

import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.TaskProvider
import org.gradle.testing.jacoco.tasks.JacocoReport
import uk.gov.onelogin.SourceSetFolder
import uk.gov.extensions.JacocoReportExt.setupReportDirectories
import uk.gov.extensions.ProjectExtensions.debugLog
import uk.gov.onelogin.filetree.fetcher.FileTreeFetcher

/**
 * Base implementation for defining the necessary properties for creating a custom Jacoco Gradle
 * task.
 *
 * @param name The name of the generated task.
 * @param project The Gradle [Project] that houses the generated Jacoco task.
 * @param classDirectoryFetcher The [FileTreeFetcher] that provides the necessary files for Jacoco
 * to analyse.
 */
abstract class JacocoCustomConfig(
    private val project: Project,
    private val classDirectoryFetcher: FileTreeFetcher,
    private val name: String,
    val testTaskName: String? = null
) {

    /**
     * Obtain the Jacoco `.exec` or `.ec` execution files to analyse, dependent on the test type.
     */
    abstract fun getExecutionData(): FileTree

    /**
     * Instance of the created Jacoco task. The architectural pattern is similar to Android
     * ViewModel handling. Updated via [generateCustomJacocoReport].
     */
    private var _createdTask: TaskProvider<JacocoReport>? = null

    /**
     * Getter value function for obtaining the internally stored Jacoco task. Throws an exception
     * if referenced before calling a variant of [generateCustomJacocoReport].
     */
    private val createdTask: TaskProvider<JacocoReport> get() = _createdTask!!

    /**
     * Create a [JacocoReport] Gradle task. Updates the [created task][createdTask] for future
     * reference if required.
     *
     * @param excludes The list of regular expression patterns used as a File filter.
     * @param dependencies The list of dependencies that the generated Jacoco task requires.
     * @param description The description for the generated task.
     * @param reportOutputDir The absolute path of the directory that shall contain the generated
     * Jacoco reports.
     * @param group The Gradle task group of the generated task. Defaults to `Jacoco`.
     * @param testTaskName The name of the associated test task. Appends the test task with
     * [finalizedBy][org.gradle.api.Task.finalizedBy] when not null.
     */
    fun generateCustomJacocoReport(
        excludes: List<String>,
        dependencies: Iterable<*>,
        description: String,
        reportOutputDir: String,
        group: String = "Jacoco"
    ): TaskProvider<JacocoReport> {
        val sourceSetFolder = SourceSetFolder(project)

        val classDirectoriesTree = classDirectoryFetcher.getProvider(excludes)

        _createdTask = if (project.tasks.findByName(name) != null) {
            project.tasks.withType(JacocoReport::class.java).named(name)
        } else {
            project.tasks.register(
                name,
                JacocoReport::class.java
            ) {
                this.dependsOn(dependencies)
                this.description = description
                this.group = group

                sourceSetFolder.sourceFolders.forEach {
                    this.additionalSourceDirs.from(it)
                    this.sourceDirectories.from(it)
                }.also {
                    project.debugLog(
                        "$name: Configured (additional) source directories: " +
                                sourceSetFolder.commaSeparatedSourceFolders
                    )
                }
                this.classDirectories.from(classDirectoriesTree).also {
                    project.debugLog(
                        "$name: Configured class directories: " +
                            "${it.files}"
                    )
                }
                this.executionData.from(this@JacocoCustomConfig.getExecutionData()).also {
                    project.debugLog(
                        "$name: Configured execution data: " +
                            "${it.files}"
                    )
                }

                this.setupReportDirectories(project, reportOutputDir)
            }
        }

        testTaskName?.let {
            project.tasks.findByName(it)?.finalizedBy(createdTask)
        }

        return createdTask
    }
}
