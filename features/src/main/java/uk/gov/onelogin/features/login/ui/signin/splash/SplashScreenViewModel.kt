package uk.gov.onelogin.features.login.ui.signin.splash

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uk.gov.android.network.online.OnlineChecker
import uk.gov.onelogin.core.navigation.data.ErrorRoutes
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.data.MainNavRoutes
import uk.gov.onelogin.core.navigation.data.SignOutRoutes
import uk.gov.onelogin.core.navigation.domain.NavRoute
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.LocalAuthStatus
import uk.gov.onelogin.core.tokens.data.initialise.AutoInitialiseSecureStore
import uk.gov.onelogin.core.tokens.domain.retrieve.GetTokenExpiry
import uk.gov.onelogin.core.utils.RefreshToken
import uk.gov.onelogin.features.appinfo.data.model.AppInfoServiceState
import uk.gov.onelogin.features.appinfo.domain.AppInfoService
import uk.gov.onelogin.features.login.domain.refresh.RefreshExchange
import uk.gov.onelogin.features.login.domain.signin.locallogin.HandleLocalLogin
import uk.gov.onelogin.features.signout.domain.SignOutError
import uk.gov.onelogin.features.signout.domain.SignOutUseCase
import javax.inject.Inject

@Suppress("LongParameterList")
@HiltViewModel
class SplashScreenViewModel
    @Inject
    constructor(
        private val navigator: Navigator,
        private val handleLocalLogin: HandleLocalLogin,
        private val appInfoService: AppInfoService,
        private val signOutUseCase: SignOutUseCase,
        private val autoInitialiseSecureStore: AutoInitialiseSecureStore,
        private val onlineChecker: OnlineChecker,
        private val refreshExchange: RefreshExchange,
        @param:RefreshToken
        private val getTokenExpiry: GetTokenExpiry,
    ) : ViewModel(),
        DefaultLifecycleObserver {
        private val _showUnlock = MutableStateFlow(false)
        val showUnlock: StateFlow<Boolean> = _showUnlock

        private val _loading = MutableStateFlow(false)
        val loading: StateFlow<Boolean> = _loading

        private val deleteData = MutableStateFlow(false)

        init {
            viewModelScope.launch {
                deleteData.collectLatest {
                    if (it) {
                        handleDeletingAllData()
                        nextScreen(LoginRoutes.AnalyticsOptIn)
                        deleteData.value = false
                    }
                }
            }
        }

        fun login(fragmentActivity: FragmentActivity) {
            viewModelScope.launch {
                autoInitialiseSecureStore.initialise(null)
                if (onlineChecker.isOnline() && getTokenExpiry() != null && getTokenExpiry() != 0L) {
                    _loading.emit(true)
                    refreshExchange.getTokens(
                        fragmentActivity,
                        handleResult = { handleLocalAuthBehaviour(it) },
                    )
                } else {
                    _loading.emit(true)
                    handleLocalLogin(
                        fragmentActivity,
                        callback = { handleLocalAuthBehaviour(it) },
                    )
                }
            }
        }

        private fun handleLocalAuthBehaviour(status: LocalAuthStatus) {
            when (status) {
                LocalAuthStatus.SecureStoreError -> {
                    nextScreen(SignOutRoutes.Info)
                }

                LocalAuthStatus.ManualSignIn -> {
                    deleteData.value = true
                }

                is LocalAuthStatus.Success ->
                    nextScreen(MainNavRoutes.Start)

                LocalAuthStatus.UserCancelled -> {
                    _loading.value = false
                    _showUnlock.value = true
                }

                LocalAuthStatus.BioCheckFailed -> {
                    // Allow user to make multiple fails... do nothing for now
                }

                // Handles ReuAuth and ClientAttestationFailure (specific behaviour to be added at a later time)
                else -> {
                    nextScreen(SignOutRoutes.Info)
                }
            }
        }

        fun navigateToDevPanel() {
            navigator.openDeveloperPanel()
        }

        fun navigateToAnalyticsOptIn() {
            navigator.navigate(LoginRoutes.AnalyticsOptIn)
        }

        fun retrieveAppInfo(onSuccess: suspend CoroutineScope.() -> Unit) {
            viewModelScope.launch {
                _loading.emit(true)
                when (appInfoService.get()) {
                    AppInfoServiceState.Offline -> navigateToOfflineError()
                    AppInfoServiceState.Unavailable -> navigateToAppUnavailableError()
                    AppInfoServiceState.UpdateRequired -> navigateToUpdateRequiredError()
                    // WHEN successful AppInfo response/ status
                    else -> {
                        onSuccess()
                        _loading.emit(false)
                    }
                }
            }
        }

        private fun navigateToAppUnavailableError() {
            navigator.navigate(ErrorRoutes.Unavailable)
        }

        private fun navigateToOfflineError() {
            navigator.navigate(ErrorRoutes.Offline)
        }

        private fun navigateToUpdateRequiredError() {
            navigator.navigate(ErrorRoutes.UpdateRequired)
        }

        private fun nextScreen(route: NavRoute) {
            val comingFromLockScreen = navigator.hasBackStack()
            val authSuccessful = route == MainNavRoutes.Start
            if (comingFromLockScreen && authSuccessful) {
                navigator.goBack()
            } else {
                navigator.goBack()
                navigator.navigate(route)
            }
        }

        @Suppress("SwallowedException")
        private suspend fun handleDeletingAllData() {
            try {
                signOutUseCase.invoke()
            } catch (e: SignOutError) {
                navigator.navigate(SignOutRoutes.Info)
            }
        }
    }
