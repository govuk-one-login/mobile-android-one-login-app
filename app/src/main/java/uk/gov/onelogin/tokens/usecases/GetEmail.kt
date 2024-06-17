package uk.gov.onelogin.tokens.usecases

import android.util.Log
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import uk.gov.onelogin.repositiories.TokenRepository

fun interface GetEmail {
    /**
     * Use case to get the email from the id token
     *
     * @return email as a string or null if it fails to retrieve it
     */
    operator fun invoke(): String?
}

@Suppress("TooGenericExceptionCaught")
class GetEmailImpl @Inject constructor(
    val tokenRepository: TokenRepository
) : GetEmail {
    override fun invoke(): String? {
        val idToken: String = tokenRepository.getTokenResponse()?.idToken ?: return null
        val email = getEmailFrom(idToken)
        return email
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun getEmailFrom(idToken: String): String? {
        try {
            val bodyEncoded = idToken.split(".")[1]
            val body = String(Base64.decode(bodyEncoded))
            val data = Json.parseToJsonElement(body)
            val email = data.jsonObject["email"].toString()
            val stripEmail = email.removeSurrounding("\"")
            return stripEmail
        } catch (e: Exception) {
            Log.e(this::class.simpleName, e.message, e)
            return null
        }
    }
}
