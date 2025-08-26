package uk.gov.onelogin.core.tokens.domain.save

import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.Base64.PaddingOption
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.tokens.data.SecureStoreException
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys

class SavePersistentIdImpl @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val saveToOpenSecureStore: SaveToOpenSecureStore,
    private val logger: Logger
) : SavePersistentId {
    @Suppress("TooGenericExceptionCaught")
    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun invoke() {
        val tokens = tokenRepository.getTokenResponse()
        tokens?.let { tokenResponse ->
            val idToken = tokenResponse.idToken
            try {
                val bodyEncoded = idToken.split(".")[1]
                val body = String(Base64.withPadding(PaddingOption.ABSENT).decode(bodyEncoded))
                val data = Json.parseToJsonElement(body)
                val id = data.jsonObject["persistent_id"]
                val stripId = id?.toString()?.removeSurrounding("\"")
                stripId?.let {
                    saveToOpenSecureStore.save(
                        key = AuthTokenStoreKeys.PERSISTENT_ID_KEY,
                        value = stripId
                    )
                }
            } catch (e: Exception) {
                val secureStoreException = SecureStoreException(e)
                logger.error(
                    secureStoreException::class.java.simpleName,
                    e.toString(),
                    secureStoreException
                )
            }
        } ?: run {
            val message = "Failed to save Persistent ID, tokens not available"
            val secureStoreException = SecureStoreException(Exception(message))
            logger.error(
                secureStoreException::class.java.simpleName,
                message,
                secureStoreException
            )
        }
    }
}
