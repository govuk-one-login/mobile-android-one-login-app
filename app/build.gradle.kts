plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("org.jlleitschuh.gradle.ktlint")
    id("uk.gov.onelogin.jvm-toolchains")
    id("com.google.dagger.hilt.android")

    kotlin("kapt")
}

android {
    namespace = rootProject.ext["appId"] as String
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    flavorDimensions += "env"
    productFlavors {
        listOf(
            "build",
            "staging",
            "production"
        ).forEach { environment ->
            create(environment) {
                var suffix = ""

                dimension = "env"

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

    sourceSets.findByName("androidTestBuild")?.let { sourceSet ->
        sourceSet.kotlin.srcDir("src/e2eTestBuild/java")
        sourceSet.java.srcDir("src/e2eTestBulid/java")
    }
}
dependencies {
    listOf(
        AndroidX.compose.ui.testJunit4,
        AndroidX.navigation.testing,
        AndroidX.test.espresso.core,
        AndroidX.test.espresso.intents,
        AndroidX.test.ext.junit,
        libs.allure.kotlin.android,
        libs.allure.kotlin.commons,
        libs.allure.kotlin.junit4,
        libs.allure.kotlin.model,
        libs.core.ktx,
        libs.hilt.android.testing,
        libs.uiautomator,
        libs.mockito.kotlin,
        libs.mockito.android
    ).forEach(::androidTestImplementation)

    listOf(
        AndroidX.compose.ui.testManifest,
        AndroidX.compose.ui.tooling
    ).forEach(::debugImplementation)

    listOf(
        AndroidX.appCompat,
        AndroidX.browser,
        AndroidX.compose.material,
        AndroidX.compose.material3,
        AndroidX.compose.ui.toolingPreview,
        AndroidX.constraintLayout,
        AndroidX.core.ktx,
        AndroidX.core.splashscreen,
        AndroidX.hilt.navigationCompose,
        AndroidX.navigation.fragmentKtx,
        AndroidX.navigation.uiKtx,
        Google.android.material,
        libs.components,
        libs.gson,
        libs.hilt.android,
        libs.kotlinx.serialization.json,
        libs.ktor.client.android,
        libs.navigation.compose,
        libs.pages,
        libs.slf4j.api,
        libs.theme,
        libs.authentication,
        projects.features
    ).forEach(::implementation)

    listOf(
        libs.hilt.android.compiler,
        libs.hilt.compiler
    ).forEach(::kapt)

    listOf(
        libs.hilt.android.compiler
    ).forEach(::kaptAndroidTest)

    listOf(
        Testing.junit.jupiter,
        Testing.junit4,
        KotlinX.coroutines.test,
        kotlin("test"),
        libs.hilt.android.testing,
        libs.ktor.client.mock,
        libs.mockito.kotlin
    ).forEach(::testImplementation)
}

kapt {
    correctErrorTypes = true
}

fun getVersionCode(): Int {
    val code = if (rootProject.hasProperty("versionCode")) {
        (rootProject.property("versionCode") as String).toInt()
    } else {
        1
    }
    println("VersionCode is set to $code")
    return code
}

fun getVersionName(): String {
    val name = if (rootProject.hasProperty("versionName")) {
        rootProject.property("versionName") as String
    } else {
        "1.0"
    }
    println("VersionName is set to $name")
    return name
}

tasks.withType<Test> {
    useJUnitPlatform()
}

task<Exec>("pullScreenshotsFromDevice") {
    mustRunAfter("connectedBuildDebugAndroidTest")

    val saveLocation = "${project.buildDir}/screenshots/"

    commandLine(
        android.adbExecutable,
        "exec-out",
        "mkdir -p /sdcard/artefacts/"
    )

    commandLine(
        android.adbExecutable,
        "exec-out",
        "run-as 'uk.gov.onelogin.test' cp -r './files/'  '/sdcard/artefacts/'"
    )

    commandLine(
        android.adbExecutable,
        "pull",
        "/sdcard/artefacts",
        saveLocation
    )
}
