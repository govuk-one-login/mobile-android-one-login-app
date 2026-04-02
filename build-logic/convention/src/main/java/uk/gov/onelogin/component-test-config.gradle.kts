package uk.gov.onelogin

import com.android.build.gradle.LibraryExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

project.afterEvaluate {
    val android = project.extensions.getByType(LibraryExtension::class.java)

    android.libraryVariants.all {
        val variantName = name.replaceFirstChar { it.uppercase() }
        val originalTestTaskName = "test${variantName}UnitTest"
        val componentTestTaskName = "componentTest${variantName}Test"
        val isDebug = buildType.name == "debug"

        val componentTestTask = tasks.register<Test>(componentTestTaskName) {
            description = "Run component tests for $variantName"
            group = "verification"

            val originalTask = tasks.named(originalTestTaskName, Test::class.java).get()
            testClassesDirs = originalTask.testClassesDirs
            classpath = originalTask.classpath
            jvmArgs = originalTask.jvmArgs

            useJUnitPlatform()
            include("**/component/**")

            reports {
                junitXml.outputLocation.set(
                    layout.buildDirectory.dir("test-results/$componentTestTaskName"),
                )
                html.outputLocation.set(
                    layout.buildDirectory.dir("reports/tests/$componentTestTaskName"),
                )
            }

            if (isDebug) {
                extensions.configure<JacocoTaskExtension> {
                    isIncludeNoLocationClasses = true
                    excludes = listOf("jdk.internal.*")
                    output = JacocoTaskExtension.Output.FILE
                    setDestinationFile(
                        layout.buildDirectory
                            .file("jacoco/$componentTestTaskName.exec").get().asFile,
                    )
                }
            }
        }

        if (isDebug) {
            val jacocoReportTaskName = "jacoco${variantName}ComponentTestReport"

            tasks.register<JacocoReport>(jacocoReportTaskName) {
                description = "Generate JaCoCo report for $variantName component tests."
                group = "Jacoco"
                dependsOn(componentTestTask)

                val sourceDirs = android.sourceSets.flatMap { it.java.srcDirs }
                sourceDirectories.from(sourceDirs)
                additionalSourceDirs.from(sourceDirs)

                classDirectories.from(
                    fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/$name")) {
                        exclude(
                            "**/R.class",
                            "**/R\$*.class",
                            "**/BuildConfig.*",
                            "**/*_Hilt*.*",
                            "**/Hilt_*.*",
                            "**/*_Factory.*",
                            "**/*_MembersInjector.*",
                        )
                    },
                )

                executionData.from(
                    layout.buildDirectory.file("jacoco/$componentTestTaskName.exec"),
                )

                val outputDir = layout.buildDirectory
                    .dir("reports/jacoco/component/$componentTestTaskName").get().asFile.absolutePath
                reports {
                    xml.required.set(true)
                    xml.outputLocation.set(file("$outputDir/report.xml"))
                    csv.required.set(true)
                    csv.outputLocation.set(file("$outputDir/report.csv"))
                    html.required.set(true)
                    html.outputLocation.set(file("$outputDir/html"))
                }
            }

            componentTestTask.configure {
                finalizedBy(jacocoReportTaskName)
            }
        }
    }
}
