package uk.gov.onelogin.core.tokens.data

data class SecureStoreException(val exception: Exception) : Exception(exception.message)
