package uk.gov.onelogin.core.tokens.data

data class LoginException(
    val exception: Throwable,
) : Exception(exception.message)
