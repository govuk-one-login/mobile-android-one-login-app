package uk.gov.onelogin.core.tokens.domain.idtoken.iss

import uk.gov.android.onelogin.core.BuildConfig
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.tokens.data.SettingsException
import uk.gov.onelogin.core.tokens.utils.JwtExtractor
import javax.inject.Inject

@Suppress("TooGenericExceptionCaught")
class ExtractAndVerifyIssuerImpl
    @Inject
    constructor(
        private val jwtExtractor: JwtExtractor,
        private val logger: Logger
    ) : ExtractAndVerifyIssuer {
        override fun verify(idToken: String): Boolean =
            try {
                val iss = jwtExtractor.extractString(idToken, "iss")
                iss?.let {
                    iss == EXPECTED_ISS
                } ?: false
            } catch (e: Exception) {
                val settingsException = SettingsException(e)
                logger.error(settingsException::class.java.simpleName, e.toString(), settingsException)
                false
            }

        companion object {
            private const val BUILD = BuildConfig.FLAVOR
            const val EXPECTED_ISS = "https://token.$BUILD.account.gov.uk"
        }
    }
