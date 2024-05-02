package uk.gov.onelogin.developer

import uk.gov.onelogin.BuildConfig

@Suppress("KotlinConstantConditions")
object DeveloperTools {
    fun isDeveloperPanelEnabled() = when (BuildConfig.FLAVOR) {
        "build" -> true
        "staging" -> true
        "production" -> false
        else -> false
    }
}
