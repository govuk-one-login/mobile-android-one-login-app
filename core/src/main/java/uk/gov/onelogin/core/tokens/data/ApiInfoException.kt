package uk.gov.onelogin.core.tokens.data

data class ApiInfoException(val exception: Exception) : Exception(exception.message)
