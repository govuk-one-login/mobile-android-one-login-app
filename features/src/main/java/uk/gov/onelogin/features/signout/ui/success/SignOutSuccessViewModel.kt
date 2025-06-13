package uk.gov.onelogin.features.signout.ui.success

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag

@HiltViewModel
class SignOutSuccessViewModel @Inject constructor(
    private val navigator: Navigator,
    val featureFlags: FeatureFlags
) : ViewModel() {
    fun navigateStart() {
        navigator.navigate(LoginRoutes.Root, true)
    }

    fun isWalletEnabled(): Boolean {
        return featureFlags[WalletFeatureFlag.ENABLED]
    }
}
