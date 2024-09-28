package uk.gov.onelogin.signOut.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import uk.gov.onelogin.login.usecase.SaveTokens
import uk.gov.onelogin.repositiories.TokenRepository

@HiltViewModel
class SignedOutInfoViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val saveTokens: SaveTokens
) : ViewModel() {
    fun resetTokens() {
        tokenRepository.clearTokenResponse()
    }

    fun saveTokens() {
        viewModelScope.launch {
            saveTokens.invoke()
        }
    }
}
