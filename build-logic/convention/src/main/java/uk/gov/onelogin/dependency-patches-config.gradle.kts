package uk.gov.onelogin

import org.gradle.accessors.dm.LibrariesForLibs

//https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()

dependencies {
    constraints {
        configurations.configureEach {
            val configuration = this@configureEach
            add(configuration.name, libs.bouncycastle.bcprov) {
                because("Earlier versions contain known vulnerabilities")
            }
        }
    }
}