package uk.gov.onelogin

import org.gradle.accessors.dm.LibrariesForLibs

//https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()

listOf(
    libs.plugins.android.application,
).forEach {
    project.plugins.apply(it.get().pluginId)
}

listOf(
    "uk.gov.pipelines.android-app-config",
    "uk.gov.onelogin.code-quality-config",
).forEach {
    project.plugins.apply(it)
}
