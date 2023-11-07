plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
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

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            compileSdkPreview = "UpsideDownCake"
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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
            "dev",
            "build",
            "staging",
            "integration",
            "production",
        ).forEach { environment ->
            create(environment) {
                dimension = "env"
                if (environment != "production") {
                    applicationIdSuffix = ".$environment"
                }
            }
        }
    }
}

dependencies {
    val composeVersion: String by rootProject.extra
    val intentsVersion: String by rootProject.extra
    val navigationVersion: String by rootProject.extra

    listOf(
        "androidx.test.ext:junit:1.1.5",
        "androidx.test.espresso:espresso-core:3.5.1",
        "androidx.compose.ui:ui-test-junit4:$composeVersion",
        "androidx.navigation:navigation-testing:$navigationVersion",
        "androidx.test.espresso:espresso-intents:$intentsVersion",
    ).forEach(::androidTestImplementation)

    listOf(
        "androidx.compose.ui:ui-test-manifest:$composeVersion",
        "androidx.compose.ui:ui-tooling:$composeVersion",
    ).forEach(::debugImplementation)

    listOf(
        "androidx.appcompat:appcompat:1.6.1",
        "androidx.browser:browser:1.5.0",
        "androidx.compose.material3:material3:1.2.0-alpha08",
        "androidx.compose.ui:ui-tooling-preview:$composeVersion",
        "androidx.constraintlayout:constraintlayout:2.1.4",
        "androidx.core:core-ktx:1.10.1",
        "androidx.core:core-splashscreen:1.0.1",
        "androidx.hilt:hilt-navigation-compose:1.0.0",
        "com.google.android.material:material:1.9.0",
        "uk.gov.android:components:1.5.0",
        "uk.gov.android:pages:1.5.0",
        "uk.gov.android:theme:1.5.0",
    ).forEach(::implementation)

    listOf(
        "androidx.navigation:navigation-fragment-ktx:$navigationVersion",
        "androidx.navigation:navigation-ui-ktx:$navigationVersion",
    ).forEach { dep: String ->
        implementation(dep) {
            because(
                "Bumping to 2.7.0 requires compile SDK 34, which the " +
                    "Android Google Plugin (AGP) would then need to be higher than 8.1.0, which " +
                    "is at the time of this writing, not released as a stable version yet.",
            )
        }
    }

    testImplementation("junit:junit:4.13.2")
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
