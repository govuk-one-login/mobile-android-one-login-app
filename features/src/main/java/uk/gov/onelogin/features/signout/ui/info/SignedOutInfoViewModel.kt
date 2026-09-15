package uk.gov.onelogin.features.signout.ui.info

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import javax.inject.Inject

@Suppress("LongParameterList")
@HiltViewModel
class SignedOutInfoViewModel
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
