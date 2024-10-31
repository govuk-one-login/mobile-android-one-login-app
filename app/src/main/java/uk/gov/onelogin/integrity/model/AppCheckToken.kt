package uk.gov.onelogin.integrity.model

import kotlinx.serialization.Serializable

@Serializable
data class AppCheckToken(
    val jwtToken: String
)