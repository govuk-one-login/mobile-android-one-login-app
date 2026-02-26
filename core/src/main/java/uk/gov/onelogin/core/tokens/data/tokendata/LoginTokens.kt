package uk.gov.onelogin.core.tokens.data.tokendata

import kotlinx.serialization.Serializable

/**
 * Data class used for the [uk.gov.onelogin.core.tokens.data.TokenRepository].
 *
 * Removes the risk of storing a `refreshToken` in memory and malicious intent to use it at a later time as it's a long-lived token.
 */
@Serializable
data class LoginTokens(
    val tokenType: String,
    val accessToken: String,
    val accessTokenExpirationTime: Long,
    val idToken: String,
)
