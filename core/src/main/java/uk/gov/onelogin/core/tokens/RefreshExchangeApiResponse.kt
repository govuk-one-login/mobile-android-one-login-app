package uk.gov.onelogin.core.tokens

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class RefreshExchangeApiResponse(
    @JsonNames("token_type")
    val tokenType: String,
    @JsonNames("access_token")
    val accessToken: String,
    @JsonNames("expires_in")
    val expiresIn: Long,
    @JsonNames("refresh_token")
    val refreshToken: String? = null,
)
