package uk.gov.jacoco

import com.android.build.api.dsl.LibraryExtension as DslLibraryExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.coverage.JacocoReportTask
import com.android.build.gradle.internal.tasks.ManagedDeviceInstrumentationTestTask
import org.gradle.configurationcache.extensions.capitalized
import uk.gov.extensions.LibraryExtensionExt.decorateExtensionWithJacoco
import uk.gov.extensions.ProjectExtensions.debugLog
import uk.gov.extensions.ProjectExtensions.libs
import uk.gov.extensions.generateDebugJacocoTasks

project.plugins.apply("uk.gov.jacoco.common-config")

/**
 * This value declares the version of the JaCoCo dependency.
 * See [change history](https://www.jacoco.org/jacoco/trunk/doc/changes.html) for more information
 */

val depJacoco: String by rootProject.extra(
    project.libs.findVersion("jacoco").get().requiredVersion
)

project.configure<DslLibraryExtension> {
    decorateExtensionWithJacoco(depJacoco).also {
        project.debugLog("Applied jacoco properties to Library")
    }
}

project.configure<LibraryExtension> {
    decorateExtensionWithJacoco(depJacoco).also {
        project.debugLog("Applied jacoco properties to Library")
    }
}

/**
 * Configure managed device test tasks to also run the managed device coverage report task created
 * by Google.
 */
project.configure<LibraryAndroidComponentsExtension> {
    onVariants(selector().withBuildType("debug")) { variant ->
        project.afterEvaluate {
            this.tasks.withType<ManagedDeviceInstrumentationTestTask>().filter {
                it.name.contains(variant.name, ignoreCase = true)
            }.forEach { instrumentationTestTask ->
                val coverageReportTaskName =
                    "createManagedDevice${variant.name.capitalized()}AndroidTestCoverageReport"
                val androidCoverageReportTask = project.tasks.named(
                    coverageReportTaskName,
                    JacocoReportTask::class.java
                )

                instrumentationTestTask.finalizedBy(androidCoverageReportTask)
                androidCoverageReportTask.configure {
                    this.mustRunAfter(instrumentationTestTask)
                }
            }
        }
    }
}

/**
 * Generate custom JaCoCo report tasks
 */
project.afterEvaluate {
    (this.findProperty("android") as? LibraryExtension)?.let { extension ->
        extension.libraryVariants.all {
            generateDebugJacocoTasks(project)
        }
    }
}
