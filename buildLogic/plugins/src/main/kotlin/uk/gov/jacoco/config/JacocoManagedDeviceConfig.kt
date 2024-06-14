package uk.gov.jacoco.config

import com.android.build.gradle.internal.tasks.ManagedDeviceInstrumentationTestTask
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.named
import uk.gov.onelogin.filetree.fetcher.FileTreeFetcher

/**
 * A [JacocoCustomConfig] implementation specifically for instrumentation tests on a gradle-managed
 * device.
 *
 * @param project The Gradle project that contains the required test task. Also generates the
 * return [FileTree].
 * @param classDirectoryFetcher The [FileTreeFetcher] that provides the class directories used for
 * reporting code coverage through Jacoco.
 */
class JacocoManagedDeviceConfig(
    private val project: Project,
    classDirectoryFetcher: FileTreeFetcher,
    name: String,
    testTaskName: String
) : JacocoCustomConfig(
    project,
    classDirectoryFetcher,
    name,
    testTaskName
) {

    override fun getExecutionData(): FileTree {
        val managedDeviceTestTask: TaskProvider<ManagedDeviceInstrumentationTestTask> =
            project.tasks.named(
                testTaskName!!,
                ManagedDeviceInstrumentationTestTask::class
            )

        val executionDirectory = managedDeviceTestTask.flatMap { connectedTask ->
            project.provider {
                connectedTask.getCoverageDirectory()
                    .asFile
                    .get()
                    .absolutePath
            }
        }

        return project.fileTree(executionDirectory) {
            setIncludes(listOf("**/*.ec"))
        }
    }
}
