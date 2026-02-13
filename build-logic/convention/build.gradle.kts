plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
}

dependencies {
    listOf(
        libs.android.build.tool,
        libs.kotlin.gradle.plugin,
        libs.ksp.gradle.plugin,
        libs.uk.gov.pipelines.plugins,
    ).forEach {
        implementation(it)
    }

    //https://github.com/gradle/gradle/issues/15383
    implementation(files((libs as Any).javaClass.superclass.protectionDomain.codeSource.location))
}
