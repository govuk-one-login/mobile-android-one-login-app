package uk.gov.onelogin.core.tokens.data

data class SignOutException(val exception: Exception) : Exception(exception.message)
