package uk.gov.onelogin.core.tokens.domain.remove

import uk.gov.android.securestore.SecureStoreAsyncV2
import uk.gov.android.securestore.error.SecureStorageErrorV2
import uk.gov.logging.api.LogTagProvider
import uk.gov.logging.api.v3.Logger
import uk.gov.onelogin.core.logging.ErrorKeys.actionKey
import uk.gov.onelogin.core.logging.ErrorKeys.componentKey
import javax.inject.Inject
import javax.inject.Named

class RemoveFromOpenSecureStoreImpl
    @Inject
    constructor(
        @param:Named("Open")
        private val secureStore: SecureStoreAsyncV2,
        private val logger: Logger,
    ) : RemoveFromOpenSecureStore,
        LogTagProvider {
        override suspend fun remove(key: String) {
            try {
                secureStore.delete(key)
            } catch (e: SecureStorageErrorV2) {
                logError(RemoveFromSecureStoreException(e))
            }
        }

        private fun logError(throwable: Throwable) {
            throwable.message?.let {
                logger.error(
                    message = it,
                    throwable = throwable,
                    actionKey(ACTION),
                    componentKey(COMPONENT),
                )
            }
        }

        internal class RemoveFromSecureStoreException(
            throwable: Throwable
        ) : RuntimeException(throwable.message)

        companion object {
            private const val COMPONENT = "secure_store"
            private const val ACTION = "Remove from open secure store "
        }
    }
