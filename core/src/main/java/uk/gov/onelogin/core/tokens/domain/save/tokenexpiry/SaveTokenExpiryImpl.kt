package uk.gov.onelogin.core.tokens.domain.save.tokenexpiry

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlinx.serialization.json.Json
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.tokens.data.RefreshTokenPayload
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys.TOKEN_SHARED_PREFS
import uk.gov.onelogin.core.utils.TimeProvider

class SaveTokenExpiryImpl @Inject constructor(
    @ApplicationContext
    context: Context,
    private val logger: Logger,
    private val systemTimeProvider: TimeProvider
) : SaveTokenExpiry {
    private val sharedPrefs = context.getSharedPreferences(
        TOKEN_SHARED_PREFS,
        Context.MODE_PRIVATE
    )

    override fun saveExp(vararg expiry: ExpiryInfo) {
        expiry.forEach {
            with(sharedPrefs.edit()) {
                putLong(it.key, it.value)
                apply()
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    override fun extractExpFromRefreshToken(jwt: String): Long {
        try {
            val jsonDecoder = Json { ignoreUnknownKeys = true }
            val definedBase64 = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)
            val jwtParts = jwt.split(".")
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
