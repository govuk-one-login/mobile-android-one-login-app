package uk.gov.onelogin.core.tokens.data

import kotlin.Exception

data class LoginException(val exception: Throwable) : Exception(exception.message)
