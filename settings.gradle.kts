import org.gradle.api.internal.provider.MissingValueException

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        // See https://github.com/JLLeitschuh/ktlint-gradle
        id("org.jlleitschuh.gradle.ktlint") version "11.6.1"

        kotlin("jvm") version "1.9.20"
        kotlin("plugin.serialization") version "1.9.21"
    }

    includeBuild("buildLogic")
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(
            "https://maven.pkg.github.com/govuk-one-login/mobile-android-ui",
            setupGithubCredentials()
        )
    }
}

plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.60.3"
}

// https://docs.gradle.org/8.0/userguide/kotlin_dsl.html#type-safe-accessors
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "OneLogin-Android"
include(":app")

refreshVersions {
    enableBuildSrcLibs()
}

fun setupGithubCredentials(): MavenArtifactRepository.() -> Unit = {
    val (credUser, credToken) = fetchGithubCredentials()
    credentials {
        username = credUser
        password = credToken
    }
}

fun fetchGithubCredentials(): Pair<String, String> {
    val gprUser = providers.gradleProperty("gpr.user")
    val gprToken = providers.gradleProperty("gpr.token")

    return try {
        gprUser.get() to gprToken.get()
    } catch (exception: MissingValueException) {
        logger.warn(
            "Could not find 'Github Package Registry' properties. Refer to the proceeding " +
                "location for instructions:\n\n" +
                "${rootDir.path}/docs/developer-setup/github-authentication.md\n",
            exception
        )

        System.getenv("GITHUB_ACTOR") to System.getenv("GITHUB_TOKEN")
    }
}
