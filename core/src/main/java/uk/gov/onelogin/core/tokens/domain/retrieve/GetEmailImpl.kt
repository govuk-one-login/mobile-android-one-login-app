package uk.gov.onelogin.core.tokens.domain.retrieve

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import uk.gov.logging.api.Logger
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.Base64.PaddingOption
import kotlin.io.encoding.ExperimentalEncodingApi

class GetEmailImpl
    @Inject
    constructor(
        private val logger: Logger
    ) : GetEmail {
        override fun invoke(idToken: String) = extractEmail(idToken)

        @Suppress("TooGenericExceptionCaught")
        @OptIn(ExperimentalEncodingApi::class)
        private fun extractEmail(idToken: String): String? {
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
    }
