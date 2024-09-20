package uk.gov.onelogin.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import uk.gov.android.authentication.TokenResponse
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.Keys
import uk.gov.onelogin.tokens.usecases.GetEmail
import uk.gov.onelogin.tokens.usecases.GetPersistentId
import uk.gov.onelogin.tokens.usecases.SaveToOpenSecureStore
import uk.gov.onelogin.tokens.usecases.SaveToSecureStore
import uk.gov.onelogin.tokens.usecases.SaveTokenExpiry

@HiltViewModel
@Suppress("LongParameterList")
class HomeScreenViewModel @Inject constructor(
    private val navigator: Navigator,
    private val tokenRepository: TokenRepository,
    private val saveToSecureStore: SaveToSecureStore,
    private val saveToOpenSecureStore: SaveToOpenSecureStore,
    private val saveTokenExpiry: SaveTokenExpiry,
    private val getPersistentId: GetPersistentId,
    getEmail: GetEmail
) : ViewModel() {
    val email = getEmail() ?: ""

    fun saveTokens() {
        val tokens = tokenRepository.getTokenResponse()
        viewModelScope.launch {
            tokens?.let { tokenResponse ->
                saveTokenExpiry(tokenResponse.accessTokenExpirationTime)
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

    fun getTokens(): TokenResponse? {
        return tokenRepository.getTokenResponse()
    }

    fun openDevPanel() {
        navigator.openDeveloperPanel()
    }
}
