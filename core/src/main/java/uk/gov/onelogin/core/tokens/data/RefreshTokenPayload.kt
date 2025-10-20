package uk.gov.onelogin.core.tokens.data

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenPayload(
    val exp: Long
)
