plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = rootProject.ext.get("appId") as String
    compileSdk = rootProject.ext.get("compileSdkVersion") as Int

    defaultConfig {
        applicationId = rootProject.ext.get("appId") as String
        minSdk = rootProject.ext.get("minSdkVersion") as Int
        targetSdk = rootProject.ext.get("targetSdkVersion") as Int
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
    ).forEach { dep: Any ->
        androidTestImplementation(dep)
    }

    listOf(
        "androidx.core:core-ktx:1.10.1",
        "androidx.appcompat:appcompat:1.6.1",
        "com.google.android.material:material:1.9.0",
        "androidx.constraintlayout:constraintlayout:2.1.4",
        "androidx.navigation:navigation-fragment-ktx:2.7.0",
        "androidx.navigation:navigation-ui-ktx:2.7.0",
    ).forEach { dep: Any ->
        implementation(dep)
    }

    testImplementation("junit:junit:4.13.2")
}
