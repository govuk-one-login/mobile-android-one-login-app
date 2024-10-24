package uk.gov.onelogin.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import uk.gov.android.authentication.TokenResponse
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.tokens.usecases.GetEmail

@HiltViewModel
@Suppress("LongParameterList")
class HomeScreenViewModel @Inject constructor(
    private val navigator: Navigator,
    private val tokenRepository: TokenRepository,
    getEmail: GetEmail
) : ViewModel() {
    val email = getEmail().orEmpty()

    fun getTokens(): TokenResponse? {
        return tokenRepository.getTokenResponse()
    }

    fun openDevPanel() {
        navigator.openDeveloperPanel()
    }
}
