package uk.gov.jacoco.config

import com.android.build.gradle.tasks.factory.AndroidUnitTest
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.kotlin.dsl.named
import uk.gov.onelogin.filetree.fetcher.FileTreeFetcher

/**
 * A [JacocoCustomConfig] implementation specifically for unit tests.
 *
 * Obtains the required Gradle task via the provided [capitalisedVariantName], passed back through
 * the [getExecutionData] function.
 *
 * @param project The Gradle project that contains the required test task. Also generates the
 * return [FileTree].
 * @param classDirectoryFetcher The [FileTreeFetcher] that provides the class directories used for
 * reporting code coverage through Jacoco.
 * @param capitalisedVariantName The TitleCase representation of the Android build variant of the
 * Gradle module. Finds the relevant test task name by using this parameter.
 */
class JacocoUnitTestConfig(
    private val project: Project,
    private val classDirectoryFetcher: FileTreeFetcher,
    private val capitalisedVariantName: String,
    name: String,
    testTaskName: String = "test${capitalisedVariantName}UnitTest"
) : JacocoCustomConfig(
    project,
    classDirectoryFetcher,
    name,
    testTaskName
) {

    override fun getExecutionData(): FileTree {
        val unitTestTask = project.tasks.named(
            testTaskName!!,
            AndroidUnitTest::class
        )
        val unitTestExecutionDataFile = unitTestTask.flatMap { utTask ->
            project.provider {
                utTask.jacocoCoverageOutputFile.get().asFile
                    .parentFile
                    .absolutePath
            }
        }
        return project.fileTree(unitTestExecutionDataFile) {
            setIncludes(listOf("${unitTestTask.name}.exec"))
        }
    }
}
