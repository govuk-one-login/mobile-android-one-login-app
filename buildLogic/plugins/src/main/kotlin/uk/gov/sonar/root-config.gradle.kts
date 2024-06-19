package uk.gov.sonar

import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra
import org.sonarqube.gradle.SonarExtension
import uk.gov.extensions.ProjectExtensions.versionName

plugins {
    id("org.sonarqube")
}

val rootSonarProperties by rootProject.extra(
    mapOf(
        "sonar.host.url" to System.getProperty("uk.gov.onelogin.sonar.host.url"),
        "sonar.token" to System.getProperty("uk.gov.onelogin.sonar.login"),
        "sonar.projectKey" to "di-mobile-android-onelogin-app",
        "sonar.projectName" to "di-mobile-android-onelogin-app",
        "sonar.projectVersion" to versionName,
        "sonar.organization" to "govuk-one-login",
        "sonar.sourceEncoding" to "UTF-8",
    ),
)

configure<SonarExtension> {
    this.setAndroidVariant("buildDebug")

    properties {
        rootSonarProperties.forEach { (key, value) ->
            property(key, value)
        }
    }
}
