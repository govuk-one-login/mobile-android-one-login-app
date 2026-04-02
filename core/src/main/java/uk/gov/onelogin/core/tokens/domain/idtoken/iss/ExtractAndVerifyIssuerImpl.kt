package uk.gov.onelogin.core.tokens.domain.idtoken.iss

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.android.onelogin.core.R
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.tokens.data.SettingsException
import uk.gov.onelogin.core.tokens.utils.JwtExtractor
import javax.inject.Inject

@Suppress("TooGenericExceptionCaught")
class ExtractAndVerifyIssuerImpl
    @Inject
    constructor(
        @param:ApplicationContext
        private val context: Context,
        private val jwtExtractor: JwtExtractor,
        private val logger: Logger,
    ) : ExtractAndVerifyIssuer {
        override fun verify(idToken: String): Boolean =
            try {
                val expectedIss = context.getString(R.string.baseStsUrl)
                val iss = jwtExtractor.extractString(idToken, "iss")
                iss?.let {
                    iss == expectedIss
                } ?: false
            } catch (e: Exception) {
                val settingsException = SettingsException(e)
                logger.error(settingsException::class.java.simpleName, e.toString(), settingsException)
                false
            }
    }
