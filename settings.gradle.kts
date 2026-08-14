import org.gradle.api.internal.provider.MissingValueException
import java.net.URI

pluginManagement {
    includeBuild("${rootProject.projectDir}/build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(
            "https://maven.pkg.github.com/govuk-one-login/*",
            setupGithubCredentials()
        )
        maven(
            "https://maven.pkg.github.com/govuk-one-login/mobile-wallet-android",
            setupGithubCredentials()
        )
        maven(
            // imposter maven repository
            "https://s3-eu-west-1.amazonaws.com/gatehillsoftware-maven/releases/"
        )
        maven {
            url = URI.create("https://raw.githubusercontent.com/iProov/android/master/maven/")
        }
        maven { url = URI.create("https://jitpack.io") }
    }
}

fun setupGithubCredentials(): MavenArtifactRepository.() -> Unit =
    {
        val (credUser, credToken) = fetchGithubCredentials()
        credentials {
            username = credUser
            password = credToken
        }
    }

fun fetchGithubCredentials(): Pair<String, String> =
    fetchGithubCredentialsFromProperties() ?:
    fetchGithubCredentialsFromEnvironment()

fun fetchGithubCredentialsFromProperties(): Pair<String, String>? {
    val gprUser = getGithubCredentialsProperty("gpr.user") ?: return null
    val gprToken = getGithubCredentialsProperty("gpr.token") ?: return null

    return gprUser to gprToken
}

fun getGithubCredentialsProperty(propertyName: String): String? = try {
    providers.gradleProperty(propertyName).get()
} catch (_: MissingValueException) {
    logger.warn(
        "Could not find 'Github Package Registry' property: $propertyName. Refer to the proceeding " +
                "location for instructions:\n\n" +
                "${rootDir.path}/docs/developerSetup/github-authentication.md\n",
    )
    null
}

fun fetchGithubCredentialsFromEnvironment(): Pair<String, String> =
    System.getenv("USERNAME") to System.getenv("TOKEN")

// https://docs.gradle.org/8.0/userguide/kotlin_dsl.html#type-safe-accessors
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "OneLogin-Android"

include(":app")
include(":core")
include(":featureflags")
include(":features")

gradle.startParameter.excludedTaskNames.addAll(listOf(":buildLogic:plugins:testClasses"))
