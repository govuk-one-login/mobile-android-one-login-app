package uk.gov.onelogin.core.tokens.domain.save

import javax.inject.Inject
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys

class SaveTokensImpl @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val saveToTokenSecureStore: SaveToTokenSecureStore
) : SaveTokens {
    override suspend fun invoke() {
        val tokens = tokenRepository.getTokenResponse()
        tokens?.let { tokenResponse ->
            saveToTokenSecureStore(
                key = AuthTokenStoreKeys.ACCESS_TOKEN_KEY,
                value = tokenResponse.accessToken
            )
            saveToTokenSecureStore(
                key = AuthTokenStoreKeys.ID_TOKEN_KEY,
                value = tokenResponse.idToken
            )
        }
    }
}
