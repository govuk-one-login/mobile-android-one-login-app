import org.gradle.api.internal.provider.MissingValueException
import java.net.URI

pluginManagement {
    includeBuild("../mobile-android-pipelines/buildLogic")
//    includeBuild("${rootProject.projectDir}/build-logic")
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

fun fetchGithubCredentials(): Pair<String, String> {
    val gprUser = providers.gradleProperty("gpr.user")
    val gprToken = providers.gradleProperty("gpr.token")

    return try {
        gprUser.get() to gprToken.get()
    } catch (exception: MissingValueException) {
        logger.warn(
            "Could not find 'Github Package Registry' properties. Refer to the proceeding " +
                "location for instructions:\n\n" +
                "${rootDir.path}/docs/developerSetup/github-authentication.md\n",
            exception
        )

        System.getenv("USERNAME") to System.getenv("TOKEN")
    }
}

// https://docs.gradle.org/8.0/userguide/kotlin_dsl.html#type-safe-accessors
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "OneLogin-Android"
//includeBuild("build-logic")
include(":app")
include(":core")
include(":featureflags")
include(":features")

//gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:plugins:testClasses"))
