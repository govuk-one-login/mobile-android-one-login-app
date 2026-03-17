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
import uk.gov.logging.api.v2.Logger
import uk.gov.logging.api.v2.errorKeys.ErrorKeys
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
import uk.gov.onelogin.features.login.domain.refresh.RefreshExchangeResult
import uk.gov.onelogin.features.login.domain.signin.locallogin.HandleLocalLogin
import uk.gov.onelogin.features.signout.domain.SignOutError
import uk.gov.onelogin.features.signout.domain.SignOutUseCase
import uk.gov.onelogin.features.wallet.domain.WalletIsEmptyUseCase
import uk.gov.onelogin.features.wallet.domain.WalletIsEmptyUseCaseImpl
import javax.inject.Inject

@Suppress("LongParameterList", "TooManyFunctions")
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
        private val walletIsEmptyUseCase: WalletIsEmptyUseCase,
        private val logger: Logger
    ) : ViewModel(),
        DefaultLifecycleObserver {
        private val _showUnlock = MutableStateFlow(false)
        val showUnlock: StateFlow<Boolean> = _showUnlock

        private val _loading = MutableStateFlow(false)
        val loading: StateFlow<Boolean> = _loading

        private val deleteData = MutableStateFlow(DeleteData())

        init {
            viewModelScope.launch {
                deleteData.collectLatest {
                    if (it.shouldDelete) {
                        handleDeletingAllData()
                        it.action()
                        // Reset state
                        deleteData.value = DeleteData()
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
                        handleResult = { handleRefreshExchangeResult(it) },
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

        private fun handleRefreshExchangeResult(result: RefreshExchangeResult) {
            when (result) {
                RefreshExchangeResult.Success -> nextScreen(MainNavRoutes.Start)

                // Handle when a user ia a first time user
                RefreshExchangeResult.FirstTimeUser -> {
                    deleteData.value = DeleteData(true) { nextScreen(LoginRoutes.AnalyticsOptIn) }
                    checkIfWalletIsEmpty()
                }

                // Handle when something went wrong during local auth
                RefreshExchangeResult.UnrecoverableError ->
                    deleteData.value = DeleteData(true) { nextScreen(SignOutRoutes.ReAuthError) }

                RefreshExchangeResult.UserCancelledBioPrompt -> {
                    _loading.value = false
                    _showUnlock.value = true
                }

                RefreshExchangeResult.ClientAttestationFailure -> nextScreen(ErrorRoutes.AppIntegrity)

                // Handles ReuAuth
                else -> {
                    nextScreen(SignOutRoutes.ReAuth)
                }
            }
        }

        private fun handleLocalAuthBehaviour(status: LocalAuthStatus) {
            when (status) {
                LocalAuthStatus.FirstTimeUser -> {
                    deleteData.value = DeleteData(true) { nextScreen(LoginRoutes.AnalyticsOptIn) }
                    checkIfWalletIsEmpty()
                }

                LocalAuthStatus.UnrecoverableError ->
                    deleteData.value = DeleteData(true) { nextScreen(SignOutRoutes.ReAuthError) }

                is LocalAuthStatus.Success ->
                    nextScreen(MainNavRoutes.Start)

                LocalAuthStatus.UserCancelledBioPrompt -> {
                    _loading.value = false
                    _showUnlock.value = true
                }

                // Handles ReuAuth and Recoverable (specific behaviour to be added at a later time)
                else -> {
                    nextScreen(SignOutRoutes.ReAuth)
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
            } catch (_: SignOutError) {
                navigator.navigate(SignOutRoutes.ReAuth)
            }
        }

        private data class DeleteData(
            val shouldDelete: Boolean = false,
            val action: () -> Unit = {}
        )

        private fun checkIfWalletIsEmpty() {
            try {
                val walletIsEmpty = walletIsEmptyUseCase.invoke()
                if (!walletIsEmpty) {
                    val walletError = WalletIsEmptyUseCaseImpl.WalletIsEmptyDataError()
                    val reason = walletError.message
                    logError(walletError, reason)
                }
            } catch (walletError: WalletIsEmptyUseCaseImpl.CouldNotDetermineIfWalletIsEmpty) {
                val reason = walletError.message ?: "could not determine if wallet is empty"
                logError(walletError, reason)
            }
        }

        private fun logError(
            e: Throwable,
            reason: String
        ) {
            logger.error(
                this.javaClass.simpleName,
                e.message ?: "error",
                e,
                ErrorKeys.StringKey("reason", reason)
            )
        }
    }
