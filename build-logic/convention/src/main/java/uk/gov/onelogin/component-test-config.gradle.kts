package uk.gov.onelogin

import com.android.build.gradle.LibraryExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

project.afterEvaluate {
    val android = project.extensions.getByType(LibraryExtension::class.java)

    android.libraryVariants.all {
        val variantName = name.replaceFirstChar { it.uppercase() }
        val variant = name
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
                    fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/$variant")) {
                        exclude(
                            "**/R.class",
                            "**/R\$*.class",
                            "**/BuildConfig.*",
                            "**/*Test*.*",
                            "**/*_Hilt*.*",
                            "**/Hilt_*.*",
                            "**/*_Factory*.*",
                            "**/*_MembersInjector.*",
                            "**/*Module*.*",
                            "**/*Dagger*.*",
                            "**/*MapperImpl*.*",
                            "**/*Companion*.*",
                        )
                    },
                    fileTree(layout.buildDirectory.dir("intermediates/javac/$variant/classes")) {
                        exclude(
                            "**/R.class",
                            "**/R\$*.class",
                            "**/BuildConfig.*",
                            "**/*Test*.*",
                        )
                    },
                )

                executionData.from(
                    layout.buildDirectory.file("jacoco/$componentTestTaskName.exec"),
                )

                val reportDir = layout.buildDirectory
                    .dir("reports/jacoco/component/$componentTestTaskName")
                    .get().asFile.absolutePath
                reports {
                    xml.required.set(true)
                    xml.outputLocation.set(file("$reportDir/report.xml"))
                    csv.required.set(true)
                    csv.outputLocation.set(file("$reportDir/report.csv"))
                    html.required.set(true)
                    html.outputLocation.set(file("$reportDir/html"))
                }
            }

            componentTestTask.configure {
                finalizedBy(jacocoReportTaskName)
            }
        }
    }
}
