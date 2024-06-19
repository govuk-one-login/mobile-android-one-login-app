package uk.gov.jacoco.tasks

import com.android.build.gradle.internal.tasks.ManagedDeviceInstrumentationTestTask
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import uk.gov.onelogin.Filters
import uk.gov.onelogin.filetree.fetcher.FileTreeFetcher
import uk.gov.jacoco.config.JacocoManagedDeviceConfig

class JacocoManagedDeviceTaskGenerator(
    private val project: Project,
    private val classDirectoriesFetcher: FileTreeFetcher,
    private val reportDirectoryPrefix: String = "${project.buildDir}/reports/jacoco"
) : JacocoTaskGenerator {
    override fun generate() {
        val testTasks: Iterable<ManagedDeviceInstrumentationTestTask> = project.tasks.withType(
            ManagedDeviceInstrumentationTestTask::class.java
        )

        testTasks.map { testTask ->
            val jacocoTaskName = "jacoco${testTask.name.capitalized()}Report"
            testTask to JacocoManagedDeviceConfig(
                project = project,
                classDirectoryFetcher = classDirectoriesFetcher,
                name = jacocoTaskName,
                testTaskName = testTask.name
            )
        }.forEach { (testTask, configuration) ->
            configuration.generateCustomJacocoReport(
                excludes = Filters.androidInstrumentationTests,
                dependencies = listOf(testTask),
                description = "Create coverage report from the " +
                    "'${testTask.name}' instrumentation tests.",
                reportOutputDir = "$reportDirectoryPrefix/managed/${testTask.name}"
            )
        }
    }
}
