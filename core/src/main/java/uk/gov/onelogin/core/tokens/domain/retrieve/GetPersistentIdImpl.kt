package uk.gov.onelogin.core.tokens.domain.retrieve

import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys
import javax.inject.Inject

class GetPersistentIdImpl
    @Inject
    constructor(
        val getFromOpenSecureStore: GetFromOpenSecureStore
    ) : GetPersistentId {
        override suspend fun invoke(): String? =
            getFromOpenSecureStore(AuthTokenStoreKeys.PERSISTENT_ID_KEY)?.get(
                AuthTokenStoreKeys.PERSISTENT_ID_KEY
            )
    }
