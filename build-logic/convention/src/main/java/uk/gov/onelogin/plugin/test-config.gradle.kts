package uk.gov.onelogin.plugin

import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import org.gradle.kotlin.dsl.configure

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

project.extensions.configure<LibraryAndroidComponentsExtension> {
    onVariants(selector().withBuildType("debug")) { variant ->
        afterEvaluate {
            extension.testTypes.get().forEach { testType ->
                val testTask = generateTestTask(
                    testType = testType,
                    variantName = variant.name,
                )
                generateCoverageTask(
                    testType = testType,
                    variantName = variant.name,
                    testTask = testTask,
                )
            }
        }
    }
}

private fun generateTestTask(
    testType: String,
    variantName: String,
): TaskProvider<Test> {
    val variantName = variantName.replaceFirstChar { it.uppercase() }
    val originalTask = tasks.named("test${variantName}UnitTest", Test::class.java)
    // e.g. "componentTestBuildDebug"
    val taskName = "${testType}Test${variantName}"

    // Register a new Test task that filters tests by the test type's package path
    return tasks.register<Test>(taskName) {
        description = "Run $testType tests for $variantName"
        group = "verification"

        // Reuse the classpath and test class directories from the original unit test task
        testClassesDirs = objects.fileCollection().from(
            originalTask.map { it.testClassesDirs },
        )
        classpath = objects.fileCollection().from(
            originalTask.map { it.classpath },
        )

        // Depend on all tasks the original test task depends on (compilation, ASM
        // transformation, resource processing etc.) without depending on the original
        // test task itself, which would cause it to run and mark classes as up-to-date
        dependsOn(originalTask.map { it.taskDependencies })

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
}

private fun generateCoverageTask(
    testType: String,
    variantName: String,
    testTask: TaskProvider<Test>,
) {
    val variantNameCaptitalised = variantName.replaceFirstChar { it.uppercase() }
    val testTypeCapitalised = testType.replaceFirstChar { it.uppercase() }
    // Generate JaCoCo coverage reports
    val jacocoReportTaskName = if (testType == "unit") {
        "jacoco${variantNameCaptitalised}Custom${testTypeCapitalised}TestReport"
    } else {
        "jacoco${variantNameCaptitalised}${testTypeCapitalised}TestReport"
    }

    tasks.register<JacocoReport>(jacocoReportTaskName) {
        description = "Generate JaCoCo report for $variantNameCaptitalised $testType tests."
        group = "Jacoco"
        // Ensure the test task runs before generating the report
        dependsOn(testTask)

        // Collect all source directories from the Android source sets
        val sourceDirs = project.extensions
            .getByType(LibraryExtension::class.java)
            .sourceSets.map { it.java.directories }
        sourceDirectories.from(sourceDirs)
        additionalSourceDirs.from(sourceDirs)

        // Include compiled Kotlin and Java classes, excluding generated/framework code
        classDirectories.from(
            fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/$variantName")) {
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
            fileTree(layout.buildDirectory.dir("intermediates/javac/$variantName/classes")) {
                exclude(
                    "**/R.class",
                    "**/R\$*.class",
                    "**/BuildConfig.*",
                    "**/*Test*.*",
                )
            },
        )

        val testTaskName = testTask.name
        // Point to the .exec file generated by the test task for coverage data
        executionData.from(
            layout.buildDirectory.file("jacoco/$testTaskName.exec"),
        )

        // Output reports to a dedicated directory per test type and task
        val reportDir = layout.buildDirectory
            .dir("reports/jacoco/$testType/$testTaskName")
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
