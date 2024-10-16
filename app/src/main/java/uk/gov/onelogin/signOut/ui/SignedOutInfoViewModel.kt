package uk.gov.onelogin.signOut.ui

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.login.usecase.SaveTokens
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.repositiories.TokenRepository
import uk.gov.onelogin.signOut.SignOutRoutes
import uk.gov.onelogin.signOut.domain.SignOutError
import uk.gov.onelogin.signOut.domain.SignOutUseCase
import uk.gov.onelogin.tokens.usecases.GetPersistentId

@HiltViewModel
class SignedOutInfoViewModel @Inject constructor(
    private val navigator: Navigator,
    private val tokenRepository: TokenRepository,
    private val saveTokens: SaveTokens,
    private val getPersistentId: GetPersistentId,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {
    fun resetTokens() {
        tokenRepository.clearTokenResponse()
    }

    fun saveTokens() {
        viewModelScope.launch {
            saveTokens.invoke()
        }
    }

    fun shouldReAuth() = navigator.hasBackStack()

    fun checkPersistentId(activity: FragmentActivity, callback: () -> Unit) {
        viewModelScope.launch {
            if (getPersistentId().isNullOrEmpty()) {
                try {
                    signOutUseCase.invoke(activity)
                    navigator.navigate(SignOutRoutes.ReAuthError, true)
                } catch (error: SignOutError) {
                    Log.e(this::class.simpleName, error.message, error)
                    navigator.navigate(LoginRoutes.SignInError, true)
                }
            } else {
                callback()
            }
        }
    }
}
