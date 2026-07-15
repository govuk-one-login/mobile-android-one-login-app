package uk.gov.onelogin

import org.gradle.accessors.dm.LibrariesForLibs

//https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()

listOf(
    "uk.gov.pipelines.android-lib-config",
    "uk.gov.onelogin.code-quality-config",
).forEach {
    project.plugins.apply(it)
}
