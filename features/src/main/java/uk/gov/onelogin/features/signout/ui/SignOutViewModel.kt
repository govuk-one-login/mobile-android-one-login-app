package uk.gov.onelogin.features.signout.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.logging.api.Logger
import uk.gov.onelogin.core.navigation.data.ErrorRoutes
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.signout.domain.SignOutError
import uk.gov.onelogin.features.signout.domain.SignOutUseCase

@HiltViewModel
class SignOutViewModel @Inject constructor(
    private val navigator: Navigator,
    private val signOutUseCase: SignOutUseCase,
    private val logger: Logger
) : ViewModel() {
    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    fun signOut() {
        _loadingState.value = true
        viewModelScope.launch {
            try {
                signOutUseCase.invoke()
                navigator.navigate(SignOutRoutes.Success)
            } catch (e: SignOutError) {
                logger.error(SignOutViewModel::class.java.simpleName, e.message.toString(), e)
                navigator.navigate(ErrorRoutes.SignOutWalletError, false)
            }
        }
    }

    fun goBack() {
        navigator.goBack()
    }
}
