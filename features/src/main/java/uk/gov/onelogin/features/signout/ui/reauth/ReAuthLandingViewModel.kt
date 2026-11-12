package uk.gov.onelogin.features.signout.ui.reauth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository

@Suppress("LongParameterList")
@HiltViewModel
class ReAuthLandingViewModel
@Inject
constructor(
    private val navigator: Navigator,
    private val tokenRepository: TokenRepository,
) : ViewModel() {
    fun resetTokens() {
        tokenRepository.clearTokenResponse()
    }

    fun shouldReAuth() = navigator.hasBackStack()
}
