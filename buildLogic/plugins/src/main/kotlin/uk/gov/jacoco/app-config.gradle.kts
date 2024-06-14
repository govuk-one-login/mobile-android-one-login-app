package uk.gov.jacoco

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.coverage.JacocoReportTask
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.build.gradle.internal.tasks.ManagedDeviceInstrumentationTestTask
import org.gradle.configurationcache.extensions.capitalized
import uk.gov.extensions.ApplicationExtensions.decorateExtensionWithJacoco
import uk.gov.extensions.ProjectExtensions.debugLog
import uk.gov.extensions.generateDebugJacocoTasks

project.plugins.apply("uk.gov.jacoco.common-config")

val depJacoco: String by rootProject.extra

project.configure<AppExtension> {
    this.decorateExtensionWithJacoco(depJacoco).also {
        project.debugLog("Applied jacoco properties to Library")
    }

    project.afterEvaluate {
        this@configure.applicationVariants.forEach {
            it.generateDebugJacocoTasks(project)
        }
    }
}

/**
 * Configure managed device test tasks to also run the managed device coverage report task created
 * by Google.
 */
project.configure<ApplicationAndroidComponentsExtension> {
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

    finalizeDsl {
        it.decorateExtensionWithJacoco(depJacoco).also {
            project.debugLog("Applied jacoco properties to Library")
        }
    }
}

project.afterEvaluate {
    (this.findProperty("android") as? BaseAppModuleExtension)?.let { extension ->
        extension.applicationVariants.forEach {
            it.generateDebugJacocoTasks(project)
        }
    }
}
