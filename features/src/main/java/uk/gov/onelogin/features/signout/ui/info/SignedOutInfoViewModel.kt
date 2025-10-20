package uk.gov.onelogin.features.signout.ui.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.localauth.domain.LocalAuthPrefResetUseCase
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.core.tokens.domain.retrieve.GetPersistentId
import uk.gov.onelogin.features.signout.domain.SignOutError
import uk.gov.onelogin.features.signout.domain.SignOutUseCase

@Suppress("LongParameterList")
@HiltViewModel
class SignedOutInfoViewModel @Inject constructor(
    private val navigator: Navigator,
    private val tokenRepository: TokenRepository,
    private val getPersistentId: GetPersistentId,
    private val signOutUseCase: SignOutUseCase,
    private val localAuthPrefResetUseCase: LocalAuthPrefResetUseCase,

    private val logger: Logger
) : ViewModel() {
    fun resetTokens() {
        tokenRepository.clearTokenResponse()
    }

    fun shouldReAuth() = navigator.hasBackStack()

    fun checkPersistentId(
        callback: () -> Unit
    ) {
        viewModelScope.launch {
            if (getPersistentId().isNullOrEmpty()) {
                try {
                    signOutUseCase.invoke()
                    navigator.navigate(SignOutRoutes.ReAuthError, true)
                } catch (error: SignOutError) {
                    logger.error(
                        error.javaClass.simpleName,
                        error.message.toString(),
                        error
                    )
                    navigator.navigate(LoginRoutes.SignInUnrecoverableError, true)
                }
            } else {
                localAuthPrefResetUseCase.reset()
                callback()
            }
        }
    }
}
