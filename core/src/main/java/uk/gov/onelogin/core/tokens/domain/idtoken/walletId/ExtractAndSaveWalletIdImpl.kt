package uk.gov.onelogin.core.tokens.domain.idtoken.walletId

import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.tokens.data.SettingsException
import uk.gov.onelogin.core.tokens.domain.save.SaveToOpenSecureStore
import uk.gov.onelogin.core.tokens.utils.JwtExtractor
import javax.inject.Inject

@Suppress("TooGenericExceptionCaught")
class ExtractAndSaveWalletIdImpl
    @Inject
    constructor(
        private val extractFromJson: JwtExtractor,
        private val saveToOpenSecureStore: SaveToOpenSecureStore,
        private val logger: Logger,
    ) : ExtractAndSaveWalletId {
        override suspend fun extractAndSave(idToken: String): String? =
            try {
                val id = extractFromJson.extractString(idToken, WALLET_ID_BASE + WALLET_ID_IDENTIFIER)
                id?.let {
                    saveToOpenSecureStore.save(WALLET_ID_KEY, it)
                }
                id
            } catch (e: Exception) {
                val settingsException = SettingsException(e)
                logger.error(settingsException::class.java.simpleName, e.toString(), settingsException)
                null
            }

        companion object {
            private const val WALLET_ID_BASE = "uk.gov.account.token"
            private const val WALLET_ID_IDENTIFIER = "/walletStoreId"
            private const val WALLET_ID_KEY = "wallet_id"
        }
    }
