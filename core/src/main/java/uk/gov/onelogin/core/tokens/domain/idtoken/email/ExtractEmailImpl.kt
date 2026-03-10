package uk.gov.onelogin.core.tokens.domain.idtoken.email

import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.tokens.data.SettingsException
import uk.gov.onelogin.core.tokens.utils.JwtExtractor
import javax.inject.Inject

@Suppress("TooGenericExceptionCaught")
class ExtractEmailImpl
    @Inject
    constructor(
        private val extractFromJson: JwtExtractor,
        private val logger: Logger,
    ) : ExtractEmail {
        override fun invoke(idToken: String) = extractEmail(idToken)

        private fun extractEmail(idToken: String): String? =
            try {
                extractFromJson.extractString(idToken, "email")
            } catch (e: Exception) {
                val settingsException = SettingsException(e)
                logger.error(settingsException::class.java.simpleName, e.toString(), settingsException)
                null
            }
    }
