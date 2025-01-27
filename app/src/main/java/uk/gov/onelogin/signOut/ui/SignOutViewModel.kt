package uk.gov.onelogin.signOut.ui

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.android.features.FeatureFlags
import uk.gov.onelogin.features.WalletFeatureFlag
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.signOut.domain.SignOutError
import uk.gov.onelogin.signOut.domain.SignOutUseCase
import uk.gov.onelogin.ui.error.ErrorRoutes

@HiltViewModel
class SignOutViewModel @Inject constructor(
    private val navigator: Navigator,
    private val signOutUseCase: SignOutUseCase,
    private val featureFlags: FeatureFlags
) : ViewModel() {
    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    val uiState: SignOutUIState
        get() = if (featureFlags[WalletFeatureFlag.ENABLED]) {
            SignOutUIState.Wallet
        } else {
            SignOutUIState.NoWallet
        }

    fun signOut(activityFragment: FragmentActivity) {
        _loadingState.value = true
        viewModelScope.launch {
            try {
                signOutUseCase.invoke(activityFragment)
                navigator.navigate(LoginRoutes.Root, true)
            } catch (e: SignOutError) {
                Log.e(this::class.simpleName, e.message, e)
                navigator.navigate(ErrorRoutes.SignOut, true)
            }
        }
    }

    fun goBack() {
        navigator.goBack()
    }
}
