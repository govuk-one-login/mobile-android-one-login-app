package uk.gov.onelogin.login.usecase

import javax.inject.Inject
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.Keys
import uk.gov.onelogin.tokens.usecases.GetPersistentId
import uk.gov.onelogin.tokens.usecases.SaveToOpenSecureStore
import uk.gov.onelogin.tokens.usecases.SaveToSecureStore

fun interface SaveTokens {
    suspend operator fun invoke()
}

class SaveTokensImpl @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val saveToSecureStore: SaveToSecureStore,
    private val saveToOpenSecureStore: SaveToOpenSecureStore,
    private val getPersistentId: GetPersistentId
) : SaveTokens {
    override suspend fun invoke() {
        val tokens = tokenRepository.getTokenResponse()
        tokens?.let { tokenResponse ->
            saveToSecureStore(
                key = Keys.ACCESS_TOKEN_KEY,
                value = tokenResponse.accessToken
            )
            tokenResponse.idToken?.let {
                getPersistentId()?.let { id ->
                    saveToOpenSecureStore(
                        key = Keys.PERSISTENT_ID_KEY,
                        value = id
                    )
                }
                saveToSecureStore(
                    key = Keys.ID_TOKEN_KEY,
                    value = it
                )
            }
        }
    }
}