package uk.gov.onelogin

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import uk.gov.onelogin.extensions.setJavaVersion

//https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()

listOf(
    libs.plugins.android.application,
    libs.plugins.kotlin.android,
).forEach {
    project.plugins.apply(it.get().pluginId)
}

listOf(
    "uk.gov.pipelines.android-app-config",
).forEach {
    project.plugins.apply(it)
}

configure<ApplicationExtension> {
    setJavaVersion()
}

configure<KotlinAndroidProjectExtension> {
    setJavaVersion()
}