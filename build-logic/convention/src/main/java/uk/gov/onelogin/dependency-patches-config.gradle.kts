package uk.gov.onelogin

import org.gradle.accessors.dm.LibrariesForLibs

//https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()

configurations.all {
    project.dependencies.constraints.apply {
        add(name, libs.bouncycastle.bcprov) {
            because("Earlier versions contain known vulnerabilities")
        }
    }
}
