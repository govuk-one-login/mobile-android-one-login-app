package uk.gov.onelogin

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType

// https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()

project.plugins.apply(libs.plugins.paparazzi.get().pluginId)

// https://github.com/cashapp/paparazzi/issues/1161
gradle.taskGraph.whenReady {
    val paparazziTaskPattern = Regex("(cleanRecord|record|verify|delete).*Paparazzi.*")
    val isScreenshotTestTask = allTasks.any { task ->
        task.name.matches(paparazziTaskPattern)
    }
    tasks.withType<Test>().configureEach {
        doFirst {
            if (isScreenshotTestTask) {
                logger.lifecycle("Running screenshot tests only in task: $name")
                include("**/*ScreenshotTest.class")
            } else {
                logger.lifecycle("Excluding screenshot tests in task: $name")
                exclude("**/*ScreenshotTest.class")
            }
        }
    }
}

