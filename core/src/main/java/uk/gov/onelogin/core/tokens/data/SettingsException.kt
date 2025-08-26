package uk.gov.onelogin.core.tokens.data

data class SettingsException(val exception: Exception) : Exception(exception.message)
