package uk.gov.onelogin.core.navigation.domain

class NavigationException(
    override val message: String,
    cause: Throwable? = null,
): IllegalStateException(message, cause)
