package uk.gov.onelogin.core.tokens.domain.save

import javax.inject.Inject
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.domain.VerifyIdToken
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys

class SaveTokensImpl @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val saveToTokenSecureStore: SaveToTokenSecureStore,
    private val saveToOpenSecureStore: SaveToOpenSecureStore,
    private val verifyIdToken: VerifyIdToken
) : SaveTokens {
    override suspend fun invoke() {
        val tokens = tokenRepository.getTokenResponse()
        tokens?.let { tokenResponse ->
            saveToTokenSecureStore(
                key = AuthTokenStoreKeys.ACCESS_TOKEN_KEY,
                value = tokenResponse.accessToken
            )
            verifyIdToken.extractPersistentIdFromIdToken(tokenResponse.idToken)
                ?.let { id ->
                    saveToOpenSecureStore.save(
                        key = AuthTokenStoreKeys.PERSISTENT_ID_KEY,
                        value = id
                    )
                }
            saveToTokenSecureStore(
                key = AuthTokenStoreKeys.ID_TOKEN_KEY,
                value = tokenResponse.idToken
            )
        }
    }
}
