import com.android.build.gradle.LibraryExtension
import uk.gov.onelogin.ext.LibraryExtensionExt.decorateDslLibraryExtensionWithJacoco
import uk.gov.onelogin.ext.LibraryExtensionExt.decorateLibraryExtensionWithJacoco
import uk.gov.onelogin.ext.ProjectExt.debugLog
import uk.gov.onelogin.ext.TestExt.decorateTestTasksWithJacoco
import uk.gov.onelogin.ext.generateDebugJacocoTasks
import com.android.build.api.dsl.LibraryExtension as DslLibraryExtension

plugins {
    jacoco
}

val jacocoVersion: String by rootProject.extra

project.tasks.withType<Test> {
    decorateTestTasksWithJacoco().also {
        project.debugLog("Applied jacoco properties to Test tasks")
    }
}

project.configure<JacocoPluginExtension> {
    this.toolVersion = jacocoVersion
    project.debugLog("Applied jacoco tool version to jacoco plugin")
}

project.configure<LibraryExtension> {
    decorateLibraryExtensionWithJacoco(jacocoVersion).also {
        project.debugLog("Applied jacoco properties to Library")
    }
}

project.configure<DslLibraryExtension> {
    decorateDslLibraryExtensionWithJacoco(jacocoVersion).also {
        project.debugLog("Applied jacoco properties to DSL Library")
    }
}

project.afterEvaluate {
    (this.findProperty("android") as LibraryExtension).let { extension ->
        extension.libraryVariants.forEach {
            it.generateDebugJacocoTasks(this)
        }

        project.debugLog("Applied jacoco custom tasks")
    }
}
