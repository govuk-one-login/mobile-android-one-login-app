package uk.gov.onelogin.core.tokens.domain

import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.Base64.PaddingOption
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import uk.gov.android.authentication.json.jwt.JwtVerifier
import uk.gov.android.network.api.ApiRequest
import uk.gov.android.network.api.ApiResponse
import uk.gov.android.network.client.GenericHttpClient
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.utils.OneLoginInjectionAnnotation

interface VerifyIdToken {
    suspend operator fun invoke(
        idToken: String,
        jwksUrl: String
    ): Boolean

    fun extractEmailFromIdToken(idToken: String): String?
    fun extractPersistentIdFromIdToken(idToken: String): String?
}

@Suppress("TooGenericExceptionCaught")
class VerifyIdTokenImpl @Inject constructor(
    private val httpClient: GenericHttpClient,
    private val verifier: JwtVerifier,
    @OneLoginInjectionAnnotation
    private val logger: Logger
) : VerifyIdToken {
    override suspend fun invoke(
        idToken: String,
        jwksUrl: String
    ): Boolean {
        val verified = isEmailValid(idToken) && isIdTokenValid(jwksUrl, idToken)
        return verified
    }

    private fun isEmailValid(idToken: String): Boolean =
        extractEmailFromIdToken(idToken) != null

    private suspend fun isIdTokenValid(
        jwksUrl: String,
        idToken: String
    ): Boolean {
        var verified = false

        val response = httpClient.makeRequest(ApiRequest.Get(jwksUrl))

        if (response is ApiResponse.Success<*>) {
            try {
                verified = useJwksResponseToVerify(response.response.toString(), idToken)
            } catch (e: Exception) {
                logger.error(this::class.java.simpleName, e.toString(), e)
            }
        }
        return verified
    }

    private fun useJwksResponseToVerify(
        response: String,
        idToken: String
    ): Boolean {
        getKeyId(idToken)?.let { keyId ->
            getKey(keyId, response)?.let { key ->
                return verifier.verify(
                    idToken,
                    key
                )
            }
        }
        return false
    }

    private fun getKey(
        keyId: String,
        jwksResponse: String
    ): String? {
        try {
            val data = Json.parseToJsonElement(jwksResponse)
            data.jsonObject["keys"]?.jsonArray?.forEach {
                if (it.jsonObject["kid"].toString() == keyId) {
                    return it.jsonObject.toString()
                }
            }
        } catch (e: Exception) {
            logger.error(this::class.java.simpleName, e.toString(), e)
        }
        return null
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun getKeyId(idToken: String): String? {
        try {
            val headerEncoded = idToken.split(".")[0]
            val header = String(Base64.decode(headerEncoded))
            val data = Json.parseToJsonElement(header)
            // the kid returned here will be surrounded by double quotes
            return data.jsonObject["kid"].toString()
        } catch (e: Exception) {
            logger.error(this::class.java.simpleName, e.toString(), e)
            return null
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    @Suppress("TooGenericExceptionCaught")
    override fun extractEmailFromIdToken(idToken: String): String? {
        try {
            val bodyEncoded = idToken.split(".")[1]
            val body =
                String(Base64.withPadding(PaddingOption.PRESENT_OPTIONAL).decode(bodyEncoded))
            val data = Json.parseToJsonElement(body)
            val email = data.jsonObject["email"]
            val stripEmail = email?.toString()?.removeSurrounding("\"")
            return stripEmail
        } catch (e: Exception) {
            logger.error(this::class.java.simpleName, e.toString(), e)
            return null
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    @Suppress("TooGenericExceptionCaught")
    override fun extractPersistentIdFromIdToken(idToken: String): String? {
        try {
            val bodyEncoded = idToken.split(".")[1]
            val body = String(Base64.withPadding(PaddingOption.ABSENT).decode(bodyEncoded))
            val data = Json.parseToJsonElement(body)
            val id = data.jsonObject["persistent_id"]
            val stripEmail = id?.toString()?.removeSurrounding("\"")
            return stripEmail
        } catch (e: Exception) {
            logger.error(this::class.java.simpleName, e.toString(), e)
            return null
        }
    }
}
