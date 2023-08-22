plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = rootProject.ext["appId"] as String
    compileSdk = rootProject.ext["compileSdkVersion"] as Int

    defaultConfig {
        applicationId = rootProject.ext["appId"] as String
        minSdk = rootProject.ext["minSdkVersion"] as Int
        targetSdk = rootProject.ext["targetSdkVersion"] as Int
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        debug {
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
        viewBinding = true
    }
}

dependencies {
    listOf(
        "androidx.test.ext:junit:1.1.5",
        "androidx.test.espresso:espresso-core:3.5.1",
    ).forEach(::androidTestImplementation)

    listOf(
        "androidx.core:core-ktx:1.10.1",
        "androidx.appcompat:appcompat:1.6.1",
        "com.google.android.material:material:1.9.0",
        "androidx.constraintlayout:constraintlayout:2.1.4",
    ).forEach(::implementation)

    listOf(
        "androidx.navigation:navigation-fragment-ktx:2.6.0",
        "androidx.navigation:navigation-ui-ktx:2.6.0",
    ).forEach { dep: String ->
        implementation(dep) {
            because(
                "Bumping to 2.7.0 requires compile SDK 34, which the " +
                    "Android Google Plugin (AGP) would then need to be higher than 8.1.0, which " +
                    "is at the time of this writing, not released as a stable version yet."
            )
        }
    }

    testImplementation("junit:junit:4.13.2")
}
