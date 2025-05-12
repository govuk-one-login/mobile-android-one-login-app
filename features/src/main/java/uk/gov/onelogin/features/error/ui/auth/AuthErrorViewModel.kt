package uk.gov.onelogin.features.error.ui.auth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag
import javax.inject.Inject

@HiltViewModel
class AuthErrorViewModel
    @Inject
    constructor(
        private val navigator: Navigator,
        featureFlags: FeatureFlags
    ) : ViewModel() {
        val walletEnabled = featureFlags[WalletFeatureFlag.ENABLED]

        fun navigateToSignIn() {
            navigator.navigate(LoginRoutes.Start, true)
        }
    }
