package uk.gov.onelogin

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import uk.gov.android.authentication.TokenResponse
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.Keys
import uk.gov.onelogin.tokens.usecases.SaveToSecureStore
import uk.gov.onelogin.tokens.usecases.SaveTokenExpiry

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val saveToSecureStore: SaveToSecureStore,
    private val saveTokenExpiry: SaveTokenExpiry
) : ViewModel() {
    fun saveTokens(context: FragmentActivity) {
        val tokens = tokenRepository.getTokenResponse()
        viewModelScope.launch {
            tokens?.let {
                saveTokenExpiry(it.accessTokenExpirationTime)
                saveToSecureStore(
                    context = context,
                    key = Keys.ACCESS_TOKENS_KEY,
                    value = it.accessToken
                )
            }
        }
    }

    fun getTokens(): TokenResponse? {
        return tokenRepository.getTokenResponse()
    }
}
