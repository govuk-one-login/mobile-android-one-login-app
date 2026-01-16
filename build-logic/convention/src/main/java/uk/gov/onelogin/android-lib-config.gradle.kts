package uk.gov.onelogin

import com.android.build.api.dsl.LibraryExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import uk.gov.onelogin.extensions.setJavaVersion

//https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()

listOf(
    "uk.gov.pipelines.android-lib-config",
).forEach {
    project.plugins.apply(it)
}

configure<LibraryExtension> {
    setJavaVersion()
}

configure<KotlinAndroidProjectExtension> {
    setJavaVersion()
}
