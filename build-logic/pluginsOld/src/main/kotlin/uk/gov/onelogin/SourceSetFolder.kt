package uk.gov.onelogin

import java.io.File
import org.gradle.api.Project

/**
 * Handles the logic for obtaining source set folders.
 *
 * @param project The Gradle [Project] that contain source set folders within `./src`.
 */
class SourceSetFolder(val src: File) {

    constructor(project: Project) : this(
        project.layout.projectDirectory.file("src").asFile
    )

    val sourceFolders: List<String>
        get() = src.listFiles(Filters.sourceFilenameFilter)
            ?.filterNotNull()
            ?.map { it.absolutePath }
            ?: listOf()

    /**
     * The production code source set folders, provided as a comma-delimited string.
     */
    val commaSeparatedSourceFolders: String
        get() = (
            sourceFolders.joinToString(separator = ",")
        )

    /**
     * The test code source set folders, provided as a comma-delimited string.
     */
    val testFolders: String
        get() = (
                src.listFiles(Filters.testFilenameFilter)
                    ?.filterNotNull()
                    ?.joinToString(separator = ",") { it.absolutePath }
                    ?: ""
                )

    /**
     * Checks to see whether the [source][src] folder exists within the [project].
     */
    fun srcExists(): Boolean = src.exists()
}
