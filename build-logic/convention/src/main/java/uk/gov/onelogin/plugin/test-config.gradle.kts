package uk.gov.onelogin.plugin

import com.android.build.api.variant.LibraryAndroidComponentsExtension
import uk.gov.pipelines.extensions.JacocoReportExt.setupReportDirectories

project.extensions.configure<LibraryAndroidComponentsExtension> {
    val testType = project.getTestType() ?: return@configure

    onVariants(selector().withBuildType("debug")) { variant ->
        afterEvaluate {
            val variantName = variant.name.replaceFirstChar { it.uppercase() }

            // Include only the tests in the specified directory
            tasks.named<Test>("test${variantName}UnitTest") {
                val includePatterns = mutableListOf("**/${testType.dirName}/**")

                if (testType == TestType.Unit) {
                    includePatterns += ("**/unitEnvironmentSpecific/**")
                }

                include(includePatterns)
            }

            // Configure the Jacoco report directory to mirror the test directory
            tasks.named<JacocoReport>("jacoco${variantName}UnitTestReport") {
                setupReportDirectories(
                    project,
                    project.layout.buildDirectory
                        .dir("reports/jacoco/${testType.dirName}/${variant.name}")
                        .map { it.asFile.absolutePath }.get()
                )
            }
        }
    }
}

private enum class TestType {
    Unit, Component
}

private fun Project.getTestType(): TestType? {
    if (!project.hasProperty("testType")) return null

    return when (val property = project.findProperty("testType")) {
        "unit" -> TestType.Unit
        "component" -> TestType.Component
        else -> error("Unrecognised test type: $property")
    }
}

private val TestType.dirName get() =
    this.name.replaceFirstChar { it.lowercase() }