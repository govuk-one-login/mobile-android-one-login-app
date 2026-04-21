package uk.gov.onelogin.plugin

import com.android.build.gradle.LibraryExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * Extension class that allows consuming modules to configure which test types
 * (e.g. "component", "contract") should have dedicated Gradle tasks created.
 *
 */
open class TestTypeExtension(objects: ObjectFactory) {
    // Internal list property so consumers can only set values via the testTypes() method
    internal val testTypes: ListProperty<String> = objects.listProperty(String::class.java)

    // Vararg convenience method for setting test types in the DSL
    fun testTypes(vararg types: String) {
        testTypes.set(types.toList())
    }
}

// Register the extension so consuming build scripts can use the testTypeConfig {} block
val extension = TestTypeExtension(project.objects)
project.extensions.add("testTypeConfig", extension)

// Use afterEvaluate to ensure the extension values and Android variants are fully resolved
project.afterEvaluate {
    // Retrieve the Android library extension to access build variants
    val android = project.extensions.getByType(LibraryExtension::class.java)

    // Iterate over each configured test type (e.g. "component", "unit")
    extension.testTypes.get().forEach { testType ->
        // Capitalise for use in task names, e.g. "component" -> "Component"
        val testTypeCapitalised = testType.replaceFirstChar { it.uppercase() }

        // Create tasks for each library variant (e.g. buildDebug, stagingRelease)
        android.libraryVariants.all {
            // e.g. "buildDebug" -> "BuildDebug"
            val variantName = name.replaceFirstChar { it.uppercase() }
            val variant = name
            // The existing unit test task to inherit classpath and test classes from
            val originalTestTaskName = "test${variantName}UnitTest"
            // e.g. "componentTestBuildDebug"
            val taskName = "${testType}Test${variantName}"
            val isDebug = buildType.name == "debug"

            // Exclude this test type's package from the original unit test task
            // so tests aren't run twice (once in the original and once in the new task)
            tasks.named(originalTestTaskName, Test::class.java) {
                exclude("**/$testType/**")
            }

            // Register a new Test task that filters tests by the test type's package path
            val testTask = tasks.register<Test>(taskName) {
                description = "Run $testType tests for $variantName"
                group = "verification"

                // Reuse the classpath and test class directories from the original unit test task
                val originalTask = tasks.named(originalTestTaskName, Test::class.java).get()
                testClassesDirs = originalTask.testClassesDirs
                classpath = originalTask.classpath
                jvmArgs = originalTask.jvmArgs

                // Depend on all tasks the original test task depends on (compilation, ASM
                // transformation, resource processing etc.) without depending on the original
                // test task itself, which would cause it to run and mark classes as up-to-date
                dependsOn(originalTask.taskDependencies)

                // Ensure this task always re-runs and is never considered up-to-date,
                // as Gradle may cache results from a previous run on CI runners
                outputs.upToDateWhen { false }

                useJUnitPlatform()
                // Only include tests under the matching package directory, e.g. "**/component/**"
                include("**/$testType/**")

                // Output test results to a dedicated directory per task
                reports {
                    junitXml.outputLocation.set(
                        layout.buildDirectory.dir("test-results/$taskName"),
                    )
                    html.outputLocation.set(
                        layout.buildDirectory.dir("reports/tests/$taskName"),
                    )
                }

                // Log a summary of passed, failed, and skipped tests after completion
                addTestListener(object : TestListener {
                    override fun beforeSuite(suite: TestDescriptor) {}
                    override fun afterSuite(suite: TestDescriptor, result: TestResult) {
                        // Only log for the root suite (the overall run)
                        if (suite.parent == null) {
                            logger.lifecycle(
                                "\n$testType tests for $variantName: " +
                                    "${result.testCount} total, " +
                                    "${result.successfulTestCount} passed, " +
                                    "${result.failedTestCount} failed, " +
                                    "${result.skippedTestCount} skipped",
                            )
                        }
                    }
                    override fun beforeTest(testDescriptor: TestDescriptor) {}
                    override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {
                        // Log each individual test with its result
                        logger.lifecycle(
                            "  ${testDescriptor.className}.${testDescriptor.name}: ${result.resultType}",
                        )
                    }
                })
            }

            // Only generate JaCoCo coverage reports for debug variants
            if (isDebug) {
                // e.g. "jacocoBuildDebugComponentTestReport"
                // This is required to allow for creating a specific unit test task as the jacoco report acocoBuildDebugUnitTestReport is automatically generated
                val jacocoReportTaskName = if (testType == "unit") {
                    "jacoco${variantName}Custom${testTypeCapitalised}TestReport"
                } else {
                    "jacoco${variantName}${testTypeCapitalised}TestReport"
                }

                tasks.register<JacocoReport>(jacocoReportTaskName) {
                    description = "Generate JaCoCo report for $variantName $testType tests."
                    group = "Jacoco"
                    // Ensure the test task runs before generating the report
                    dependsOn(testTask)

                    // Collect all source directories from the Android source sets
                    val sourceDirs = android.sourceSets.flatMap { it.java.srcDirs }
                    sourceDirectories.from(sourceDirs)
                    additionalSourceDirs.from(sourceDirs)

                    // Include compiled Kotlin and Java classes, excluding generated/framework code
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

                    // Point to the .exec file generated by the test task for coverage data
                    executionData.from(
                        layout.buildDirectory.file("jacoco/$taskName.exec"),
                    )

                    // Output reports to a dedicated directory per test type and task
                    val reportDir = layout.buildDirectory
                        .dir("reports/jacoco/$testType/$taskName")
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

                // Automatically generate the JaCoCo report after the test task completes
                testTask.configure {
                    finalizedBy(jacocoReportTaskName)
                }
            }
        }
    }
}
