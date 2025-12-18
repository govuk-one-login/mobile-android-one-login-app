package uk.gov.onelogin.core.tokens.domain.save.tokenexpiry

import java.nio.charset.StandardCharsets
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlinx.serialization.json.Json
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.tokens.data.RefreshTokenPayload
import uk.gov.onelogin.core.tokens.domain.save.SaveToOpenSecureStore
import uk.gov.onelogin.core.utils.TimeProvider

class SaveTokenExpiryImpl @Inject constructor(
    private val logger: Logger,
    private val systemTimeProvider: TimeProvider,
    private val secureStore: SaveToOpenSecureStore
) : SaveTokenExpiry {
    override suspend fun saveExp(vararg expiry: ExpiryInfo) {
        expiry.forEach {
            secureStore.save(it.key, it.value)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    override fun extractExpFromRefreshToken(tokenJwt: String): Long {
        try {
            val jsonDecoder = Json { ignoreUnknownKeys = true }
            val definedBase64 = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)
            val jwtParts = tokenJwt.split(".")
            val payloadJson = String(definedBase64.decode(jwtParts[1]), StandardCharsets.UTF_8)
            val expiry: RefreshTokenPayload = jsonDecoder.decodeFromString(payloadJson)
            return expiry.exp
        } catch (e: Throwable) {
            logger.error(
                e.javaClass.simpleName,
                e.message ?: EXTRACT_REFRESH_TOKEN_EXP,
                e
            )
            val thirtyDaysExp = systemTimeProvider.thirtyDaysFromNowTimestampInSeconds()
            return thirtyDaysExp
        }
    }

    companion object {
        const val EXTRACT_REFRESH_TOKEN_EXP = "Error when extracting the refresh token expiry."
    }
}
