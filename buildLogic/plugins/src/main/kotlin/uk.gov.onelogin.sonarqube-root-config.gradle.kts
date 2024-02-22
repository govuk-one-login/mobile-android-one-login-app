import org.sonarqube.gradle.SonarExtension

plugins {
    id("org.sonarqube")
}

/**
 * Defined within the git repository's `build.gradle.kts` file
 */
val versionName: String by rootProject.extra

val rootSonarProperties by rootProject.extra(
    mapOf(
        "sonar.host.url" to System.getProperty("uk.gov.onelogin.sonar.host.url"),
        "sonar.login" to System.getProperty("uk.gov.onelogin.sonar.login"),
        "sonar.projectKey" to "di-mobile-android-one-login",
        "sonar.projectName" to "di-mobile-android-one-login",
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
