package uk.gov.onelogin.plugin

import com.android.build.api.variant.LibraryAndroidComponentsExtension
import uk.gov.pipelines.extensions.JacocoReportExt.setupReportDirectories

project.extensions.configure<LibraryAndroidComponentsExtension> {
    val testDir = project.findProperty("testDir")
        ?: return@configure

    onVariants(selector().withBuildType("debug")) { variant ->
        afterEvaluate {
            val variantName = variant.name.replaceFirstChar { it.uppercase() }

            // Include only the tests in the specified directory
            tasks.named<Test>("test${variantName}UnitTest") {
                include("**/$testDir/**")
            }

            // Configure the Jacoco report directory to mirror the test directory
            tasks.named<JacocoReport>("jacoco${variantName}UnitTestReport") {
                setupReportDirectories(
                    project,
                    project.layout.buildDirectory
                        .dir("reports/jacoco/$testDir/${variant.name}" )
                        .map { it.asFile.absolutePath}.get()
                )
            }
        }
    }
}