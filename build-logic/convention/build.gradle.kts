plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.serialization)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
}

dependencies {
    listOf(
        libs.android.build.tool,
        libs.kotlin.gradle.plugin,
        libs.ksp.gradle.plugin,
        libs.uk.gov.pipelines.plugins,
    ).forEach {
        implementation(it)
    }

    // Transitive dependencies of the build/plugin tooling (AGP, Kotlin Gradle plugin, the
    // shared pipelines plugins) flagged by Dependabot against `settings.gradle.kts`. These
    // only exist on the build-logic / plugin classpath, never in the shipped app, but they
    // are constrained here to patched versions to clear the alerts.
    constraints {
        implementation(libs.patch.guava) {
            because("Earlier versions contain known vulnerabilities (GHSA-xxph-c9ww-hj94)")
        }
        // The bouncycastle modules must move together — mixing bcprov 1.85 with older
        // bcutil/bcpkix causes a NoSuchFieldError at runtime.
        implementation(libs.bundles.tooling.security.bouncycastle) {
            because("Earlier versions contain known vulnerabilities (GHSA-9pwp-9qqc-pr26, GHSA-qp49-qgx5-5m26)")
        }
        implementation(libs.patch.logback.core) {
            because("Earlier versions contain known vulnerabilities (GHSA-jhq6-gfmj-v8fx)")
        }
    }

    //https://github.com/gradle/gradle/issues/15383
    implementation(files((libs as Any).javaClass.superclass.protectionDomain.codeSource.location))
}
