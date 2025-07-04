import com.android.build.api.variant.BuildConfigField

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)
    kotlin("kapt")
    id("uk.gov.onelogin.jvm-toolchains")
    id("uk.gov.jacoco.app-config")
    id("uk.gov.sonar.module-config")
    id("uk.gov.onelogin.emulator-config")
}

apply(from = "${rootProject.extra["configDir"]}/detekt/config.gradle")
apply(from = "${rootProject.extra["configDir"]}/ktlint/config.gradle")

android {
    namespace = "uk.gov.android.onelogin"
    compileSdk = rootProject.ext["compileSdkVersion"] as Int

    defaultConfig {
        applicationId = rootProject.ext["appId"] as String
        minSdk = rootProject.ext["minSdkVersion"] as Int
        targetSdk = rootProject.ext["targetSdkVersion"] as Int
        versionCode = rootProject.ext["versionCode"] as Int
        versionName = rootProject.ext["versionName"] as String
        testInstrumentationRunner = "uk.gov.onelogin.InstrumentationTestRunner"
    }

    signingConfigs {
        create("release") {
            val configDir = rootProject.extra["configDir"] as String

            storeFile = file("$configDir/keystore.jks")

            storePassword = System.getenv("SIGNING_STORE_PASSWORD")
            keyAlias = System.getenv("SIGNING_KEY_ALIAS")
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }
    flavorDimensions += "env"
    productFlavors {
        listOf(
            "build",
            "staging",
            "integration",
            "production"
        ).forEach { environment ->
            create(environment) {
                var suffix = ""

                dimension = "env"
                if (environment == "integration") {
                    matchingFallbacks.add("production")
                }

                if (environment != "production") {
                    suffix = ".$environment"
                    applicationIdSuffix = ".$environment"
                }

                val packageName = "${project.android.namespace}$suffix"

                manifestPlaceholders["flavorSuffix"] = suffix
                manifestPlaceholders["appAuthRedirectScheme"] = packageName
            }
        }
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
            it.testLogging {
                events = setOf(
                    org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                    org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
                    org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
                )
            }
        }
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }

    sourceSets.findByName("androidTestBuild")?.let { sourceSet ->
        sourceSet.kotlin.srcDir("src/e2eTestBuild/java")
        sourceSet.java.srcDir("src/e2eTestBulid/java")
    }

    bundle {
        language {
            enableSplit = false
        }
    }

    packaging {
        resources.excludes += setOf(
            "META-INF/LICENSE-notice.md",
            "META-INF/versions/9/OSGI-INF/MANIFEST.MF",
            "META-INF/LICENSE.md"
        )
    }
}

androidComponents {
    onVariants {
        if (it.buildType == "debug") {
            if (it.flavorName == "build") {
                it.buildConfigFields.put(
                    "AppCheckDebugSecret",
                    BuildConfigField(
                        "String",
                        "\"" + rootProject.ext["debugBuildAppCheckToken"] as String + "\"",
                        "debug token"
                    )
                )
            } else if (it.flavorName == "staging") {
                it.buildConfigFields.put(
                    "AppCheckDebugSecret",
                    BuildConfigField(
                        "String",
                        "\"" + rootProject.ext["debugStagingAppCheckToken"] as String + "\"",
                        "debug token"
                    )
                )
            } else {
                it.buildConfigFields.put(
                    "AppCheckDebugSecret",
                    BuildConfigField(
                        "String",
                        "\"\"",
                        "debug token"
                    )
                )
            }
        }
    }
}

dependencies {
    listOf(
        libs.androidx.compose.ui.junit4,
        libs.androidx.navigation.testing,
        libs.androidx.espresso.intents,
        libs.androidx.espresso.core,
        libs.androidx.test.ext.junit,
        libs.test.core.ktx,
        libs.hilt.android.testing,
        libs.uiautomator,
        libs.mockito.kotlin,
        libs.mockito.android
    ).forEach(::androidTestImplementation)

    listOf(
        libs.androidx.compose.ui.tooling,
        libs.androidx.compose.ui.test.manifest
    ).forEach(::debugImplementation)

    listOf(
        libs.androidx.appcompat,
        libs.androidx.browser,
        libs.androidx.biometric,
        platform(libs.androidx.compose.bom),
        libs.androidx.compose.material,
        libs.androidx.compose.material3,
        libs.androidx.compose.ui.tooling.preview,
        libs.androidx.constraintlayout,
        libs.androidx.core.ktx,
        libs.androidx.hilt.navigation.compose,
        libs.androidx.lifecycle.viewmodel.compose,
        libs.androidx.lifecycle.runtime.compose,
        libs.bundles.gov.uk,
        libs.gson,
        libs.hilt.android,
        libs.kotlinx.serialization.json,
        libs.kotlinx.coroutines.core,
        libs.ktor.client.android,
        libs.navigation.compose,
        libs.slf4j.api,
        libs.jose4j,
        projects.featureflags,
        projects.core,
        projects.features,
        libs.runtime.livedata,
        platform(libs.firebase.bom),
        libs.bundles.firebase,
        libs.androidx.biometric,
        libs.bundles.cri.orchestrator.bundle
    ).forEach(::implementation)

    implementation(libs.wallet.sdk) {
        exclude(group = "uk.gov.android", module = "network")
        exclude(group = "uk.gov.securestore", module = "app")
    }

    listOf(
        libs.hilt.android.compiler,
        libs.hilt.compiler
    ).forEach(::kapt)

    listOf(
        libs.hilt.android.compiler
    ).forEach(::kaptAndroidTest)

    listOf(
        kotlin("test"),
        libs.hilt.android.testing,
        libs.ktor.client.mock,
        libs.mockito.kotlin,
        libs.junit.jupiter,
        libs.junit.jupiter.params,
        libs.junit.vintage.engine,
        platform(libs.junit.bom),
        libs.kotlinx.coroutines.test,
        libs.classgraph,
        libs.roboelectric,
        libs.junit,
        libs.androidx.test.orchestrator,
        libs.androidx.test.ext.junit
    ).forEach(::testImplementation)

    testRuntimeOnly(libs.junit.jupiter.engine)

    listOf(
        libs.androidx.test.orchestrator
    ).forEach {
        androidTestUtil(it)
    }
}

kapt {
    correctErrorTypes = true
}

fun getVersionCode(): Int {
    val code =
        if (rootProject.hasProperty("versionCode")) {
            (rootProject.property("versionCode") as String).toInt()
        } else {
            1
        }
    return code
}

fun getVersionName(): String {
    val name =
        if (rootProject.hasProperty("versionName")) {
            rootProject.property("versionName") as String
        } else {
            "1.0"
        }
    return name
}
