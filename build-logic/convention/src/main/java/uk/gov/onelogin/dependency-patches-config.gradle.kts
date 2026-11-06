package uk.gov.onelogin

import org.gradle.accessors.dm.LibrariesForLibs

//https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()

dependencies {
    constraints {
        configurations.configureEach {
            val configuration = this@configureEach

            // Some special/legacy configurations (e.g. `archives`) do not allow declaring
            // dependency constraints. Only target configurations that accept declarations.
            if (!configuration.isCanBeDeclared) {
                return@configureEach
            }

            add(configuration.name, libs.bouncycastle.bcprov) {
                because("Earlier versions contain known vulnerabilities")
            }

            // Transitive netty (via AGP's Unified Test Platform / grpc-netty) is flagged by
            // a batch of Dependabot CVEs. netty is not on any app runtime classpath, but the
            // whole group is aligned to a patched version so the modules stay consistent.
            add(configuration.name, libs.bundles.tooling.security.netty) {
                because("Earlier versions contain known vulnerabilities")
            }

            // Transitive dependencies of build/test tooling (Android Lint, ktlint, annotation
            // processors) flagged by Dependabot. None are on the app runtime classpath; the
            // constraints simply raise the tooling's transitives to patched versions.
            add(configuration.name, libs.bundles.tooling.security.other) {
                because("Earlier versions contain known vulnerabilities")
            }
        }
    }
}
